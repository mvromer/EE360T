package ee360t.controlflow.trace.agent;

import ee360t.controlflow.utility.ControlFlowAnalyzer;
import ee360t.controlflow.utility.IndexGraph;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class TraceTransformer implements ClassFileTransformer {
    private List<String> prefixesToTrace;
    private boolean verbose;

    private final String startNewTraceOwner = Type.getInternalName( TraceRegistry.class );
    private final String startNewTraceName = "startNewTrace";
    private Type startNewTraceType;

    private final String visitNodeOwner = Type.getInternalName( TraceRegistry.class );
    private final String visitNodeName = "visitNode";
    private Type visitNodeType;

    public TraceTransformer( List<String> prefixesToTrace, boolean verbose ) {
        this.prefixesToTrace = new ArrayList<>( prefixesToTrace.size() );
        this.verbose = verbose;

        // Convert prefixes to internal name format.
        for( String prefix : prefixesToTrace ) {
            this.prefixesToTrace.add( prefix.replace( '.', '/' ) );
        }

        try {
            // Get Type objects for the methods we want to instrument.
            startNewTraceType = Type.getType( TraceRegistry.class.getMethod( startNewTraceName,
                    String.class, String.class, String.class  ) );
            visitNodeType = Type.getType( TraceRegistry.class.getMethod( visitNodeName, int.class ) );
        }
        catch( NoSuchMethodException ex ) {
            System.err.println( "Error getting method used for instrumentation." );
            ex.printStackTrace( System.err );
            System.exit( 1 );
        }
    }

    @Override
    public byte[] transform( ClassLoader loader, String className, Class<?> classBeingRedefined,
                             ProtectionDomain protectionDomain, byte[] classfileBuffer )
        throws IllegalClassFormatException {
        boolean shouldTraceClass = shouldTraceClass( className );
        boolean modifiedClass = false;

        ClassReader classReader = new ClassReader( classfileBuffer );
        ClassNode classNode = new ClassNode( ASM4 );
        classReader.accept( classNode, 0 );

        if( verbose && shouldTraceClass ) {
            TraceClassVisitor classVisitor = new TraceClassVisitor( new PrintWriter( System.out ) );
            classNode.accept( classVisitor );
        }

        for( MethodNode method : classNode.methods ) {
            if( shouldTraceClass ) {
                System.out.println( String.format( "Instrumenting: %s.%s%s", className, method.name, method.desc ) );
                instrumentTracedMethod( classNode, method );
                modifiedClass = true;
            }
            else if( isJunitTestMethod( method ) ) {
                System.out.println( String.format( "Instrumenting: %s.%s%s\n", className, method.name, method.desc ) );
                instrumentJunitTestMethod( classNode, method );
                modifiedClass = true;
            }
        }

        if( !modifiedClass )
            return null;

        if( verbose && shouldTraceClass ) {
            TraceClassVisitor classVisitor = new TraceClassVisitor( new PrintWriter( System.out ) );
            classNode.accept( classVisitor );
        }

        ClassWriter classWriter = new ClassWriter( ClassWriter.COMPUTE_FRAMES );
        classNode.accept( classWriter );
        return classWriter.toByteArray();
    }

    private boolean shouldTraceClass( String className ) {
        for( String prefix : this.prefixesToTrace ) {
            if( className.startsWith( prefix ) ) {
                return true;
            }
        }

        return false;
    }

    private boolean isJunitTestMethod( MethodNode method ) {
        final String junitTestInternalName = "org/junit/Test";

        if( method.visibleAnnotations == null )
            return false;

        for( AnnotationNode annotation : method.visibleAnnotations ) {
            String annotationInternalName = Type.getType( annotation.desc ).getInternalName();
            if( annotationInternalName.equals( junitTestInternalName ) )
                return true;
        }

        return false;
    }

    private void instrumentTracedMethod( ClassNode owner, MethodNode method ) {
        try {
            IndexGraph controlFlow = ControlFlowAnalyzer.buildControlFlow( owner.name, method );
            TraceRegistry.setControlFlow( controlFlow, owner.name, method.name, method.desc );
            TraceRegistry.setSourceFileName( owner.name, owner.sourceFile );

            // For each node, we want to record when we're about to visit it. We visit the nodes in reverse index order
            // so that when we insert our instrumentation for a given node, we don't invalidate the instruction indices
            // used for insertion on subsequent loop iterations.
            //
            // For ENTRY and EXIT nodes, since they're artificial, and thus execution will never naturally reach them,
            // we need to handle them as special cases so that we make sure we end up visiting them at the correct time.
            //
            // If an instruction has the ENTRY node as a predecessor, we make sure we visit the ENTRY node first. If an
            // instruction has the EXIT node as a successor, we make sure we visit the EXIT node after we visit the node
            // corresponding to the instruction.
            //
            // Note that we have to add the instrumentation for the EXIT node BEFORE the actual instruction preceding
            // our EXIT node. This is because that instruction will actually transfer control flow to some other
            // method/routine in the program.
            //
            // For each node we visit, we compute a globally unique node ID based on the node's enclosing class and
            // method, which is determined by the method name and method descriptor. This is an optimization to minimize
            // the overhead of recording a node visitation.
            //
            // Our instrumentation is basically two instructions per node we visit: push the node's global ID onto the
            // operand stack and call our static visitNode method. We could cheat and use the LDC instruction for all
            // possible integer values, but that's not ideal. To better match what a typical Java compiler would emit,
            // we need to use BIPUSH, SIPUSH, or one of the ICONST_<i> instructions, depending on the value of the
            // global ID we're passing to the called method.
            //
            Integer[] nodes = controlFlow.getNodes().toArray( new Integer[0] );
            Collections.reverse( Arrays.asList( nodes ) );

            for( int iNode : nodes ) {
                if( iNode == IndexGraph.ENTRY || iNode == IndexGraph.EXIT )
                    continue;

                // List of instructions that will represent our instrumentation.
                InsnList instrumentation = new InsnList();

                // If this node has the ENTRY node as a predecessor, make sure we visit the ENTRY node.
                if( controlFlow.getPredecessors( iNode ).contains( IndexGraph.ENTRY ) ) {
                    int globalEntryId = TraceRegistry.getGlobalId( owner.name, method.name, method.desc, IndexGraph.ENTRY );
                    instrumentation.add( getPushGlobalIdInstruction( globalEntryId ) );
                    instrumentation.add( getInvokeVisitNodeInstruction() );
                }

                // Get (or compute) a global ID for this node based on the node's enclosing class and method (which is
                // determined by the method name and method descriptor). This is an optimization to minimize overhead
                // of recording a node visitation.
                int globalId = TraceRegistry.getGlobalId( owner.name, method.name, method.desc, iNode );
                instrumentation.add( getPushGlobalIdInstruction( globalId ) );
                instrumentation.add( getInvokeVisitNodeInstruction() );

                // If this node has the EXIT node as a successor, make sure we visit the EXIT node.
                if( controlFlow.getSuccessors( iNode ).contains( IndexGraph.EXIT ) ) {
                    int globalExitId = TraceRegistry.getGlobalId( owner.name, method.name, method.desc,
                        IndexGraph.EXIT );
                    instrumentation.add( getPushGlobalIdInstruction( globalExitId ) );
                    instrumentation.add( getInvokeVisitNodeInstruction() );
                }

                AbstractInsnNode insertPoint = method.instructions.get( iNode );
                method.instructions.insertBefore( insertPoint, instrumentation );
            }
        }
        catch( Exception ex ) {
            System.err.println( String.format( "Error instrumenting traced method %s.%s%s\n",
                    owner.name, method.name, method.desc ) );
            ex.printStackTrace( System.err );
        }
    }

    private AbstractInsnNode getPushGlobalIdInstruction( int globalId ) {
        // NOTE: We can simplify these checks because we know all global IDs will be nonnegative.
        switch( globalId ) {
            case 0:
                return new InsnNode( ICONST_0 );
            case 1:
                return new InsnNode( ICONST_1 );
            case 2:
                return new InsnNode( ICONST_2 );
            case 3:
                return new InsnNode( ICONST_3 );
            case 4:
                return new InsnNode( ICONST_4 );
            case 5:
                return new InsnNode( ICONST_5 );
            default:
                if( globalId <= Byte.MAX_VALUE )
                    return new IntInsnNode( BIPUSH, globalId );
                else if( globalId <= Short.MAX_VALUE )
                    return new IntInsnNode( SIPUSH, globalId );
                else
                    return new LdcInsnNode( globalId );
        }
    }

    private AbstractInsnNode getInvokeVisitNodeInstruction() {
        return new MethodInsnNode( INVOKESTATIC, visitNodeOwner, visitNodeName,
                visitNodeType.getDescriptor(), false );
    }

    private void instrumentJunitTestMethod( ClassNode owner, MethodNode method ) {
        try {
            IndexGraph controlFlow = ControlFlowAnalyzer.buildControlFlow( owner.name, method );

            // We expect the entry node to point to the first instruction of the method, and there better be only one
            // initial instruction in any given method.
            Set<Integer> startNodes = controlFlow.getSuccessors( IndexGraph.ENTRY );
            assert startNodes.size() == 1;

            int iStartInstruction = startNodes.iterator().next();
            InsnList instrumentation = new InsnList();
            instrumentation.add( new LdcInsnNode( owner.name ) );
            instrumentation.add( new LdcInsnNode( method.name ) );
            instrumentation.add( new LdcInsnNode( method.desc ) );
            instrumentation.add( new MethodInsnNode( INVOKESTATIC, startNewTraceOwner, startNewTraceName,
                    startNewTraceType.getDescriptor(), false ) );
            method.instructions.insertBefore( method.instructions.get( iStartInstruction ), instrumentation );
        }
        catch( Exception ex ) {
            System.err.println( String.format( "Error instrumenting JUnit test method %s.%s%s\n",
                    owner.name, method.name, method.desc ) );
            ex.printStackTrace( System.err );
        }
    }
}
