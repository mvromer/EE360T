package ee360t.controlflow.utility;

import ee360t.controlflow.model.JavaClass;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.ASM4;

public class MethodBuilder extends MethodNode {
    JavaClass ownerClass;

    public MethodBuilder( JavaClass ownerClass, int access, String name, String desc, String signature,
                          String[] exceptions ) {
        super( ASM4, access, name, desc, signature, exceptions );
        this.ownerClass = ownerClass;
    }

    @Override
    public void visitEnd() {
        // ASM injects label, line number, and frame nodes in the instruction list, even though they aren't real
        // instructions. We don't want those fake instructions in the control flow graph we use for this project, but Of
        // course simply removing them prior to running the analyzer that builds the CFG would've been too simple.
        //
        // Instead, we build up the index graph and then do a pass on it after running the analyzer to prune out those
        // nodes that correspond to the fake instructions we don't want. We don't bother rebasing indices, which has the
        // advantage of being able to look up the real instruction nodes easily from our pruned index graph at a later
        // time.
        //

        // Use a custom analyzer that will record the control flow edges found by the ASM method analyzer.
        ControlFlowAnalyzer analyzer = new ControlFlowAnalyzer();
        IndexGraph controlFlow = null;

        try {
            analyzer.analyze( ownerClass.getName(), this );
            controlFlow = analyzer.getControlFlow();

            // Add an edge from the well-defined entry node to the first instruction in the control flow graph, which is
            // always at index 0. Do this now so that the entry node points to the first real instruction after pruning.
            controlFlow.addEdge( IndexGraph.ENTRY, 0 );

            // TODO: Remove me.
//            printInstructions();
//            System.out.println( "Before pruning:" );
//            controlFlow.printDot( System.out );

            // Go through the list of instructions and remove nodes from our control flow graph corresponding to fake
            // instructions signifying labels, line numbers, and stack frames.
            int iInstruction = 0;
            ListIterator<AbstractInsnNode> instructionIter = instructions.iterator();
            while( instructionIter.hasNext() ) {
                AbstractInsnNode currentInstruction = instructionIter.next();
                int instructionType = currentInstruction.getType();
                int opcode = currentInstruction.getOpcode();

                if( instructionType == AbstractInsnNode.LABEL ||
                    instructionType == AbstractInsnNode.LINE ||
                    instructionType == AbstractInsnNode.FRAME ) {
                    controlFlow.removeNode( iInstruction );
                    // TODO: Remove me.
//                    System.out.println( "Removing instruction at index " + iInstruction );
                }
                else if( opcode == Opcodes.ARETURN ||
                    opcode == Opcodes.DRETURN ||
                    opcode == Opcodes.FRETURN ||
                    opcode == Opcodes.IRETURN ||
                    opcode == Opcodes.LRETURN ||
                    opcode == Opcodes.RETURN ) {
                    // Add an edge from this return statement to the well-defined exit node for this method.
                    controlFlow.addEdge( iInstruction, IndexGraph.EXIT );
                }

                ++iInstruction;
            }

            // TODO: Remove me.
//            System.out.println( "After pruning:" );
//            controlFlow.printDot( System.out );
        }
        catch( AnalyzerException ex ) {
            throw new RuntimeException( ex );
        }

        ownerClass.addMethod( name, desc, controlFlow );
    }

    private void printInstructions() {
        Textifier t = new Textifier();
        TraceMethodVisitor tmv = new TraceMethodVisitor( t );
        accept( tmv );
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        t.print( pw );
        pw.flush();
        System.out.println();
        System.out.println( sw.toString() );
    }
}
