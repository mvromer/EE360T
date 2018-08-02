package ee360t.controlFlowGenerator;

import ee360t.controlFlowGenerator.utility.IndexGraph;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM4;

public class MethodBuilder extends MethodNode {
    Class ownerClass;

    public MethodBuilder( Class ownerClass, int access, String name, String desc, String signature,
                          String[] exceptions ) {
        super( ASM4, access, name, desc, signature, exceptions );
        this.ownerClass = ownerClass;
    }

    @Override
    public void visitEnd() {
        System.out.println( "Method " + name );
        IndexGraph instructionGraph = new IndexGraph();
        Analyzer<BasicValue> a = new Analyzer<BasicValue>( new BasicInterpreter() ) {
            @Override
            protected void newControlFlowEdge( int iFrom, int iTo ) {
                instructionGraph.addEdge( iFrom, iTo );
            }
        };

        try {
            a.analyze( ownerClass.getName(), this );
            Map<Integer, Set<Integer>> edges = instructionGraph.getEdges();
            for( int iFrom : edges.keySet() ) {
                for( int iTo : edges.get( iFrom ) ) {
                    System.out.println( "edge from " + iFrom + " to " + iTo );
                }
            }
        }
        catch( AnalyzerException ex ) {
            System.err.println( "Analyzer error" );
            ex.printStackTrace( System.err );
        }

        System.out.println();

//        Textifier t = new Textifier();
//        TraceMethodVisitor tmv = new TraceMethodVisitor( t );
//        accept( tmv );
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter( sw );
//        t.print( pw );
//        pw.flush();
        //System.out.println( sw.toString() );
        Textifier t = new Textifier();
        TraceMethodVisitor tmv = new TraceMethodVisitor( t );
        for( AbstractInsnNode ain : instructions.toArray() ) {
            ain.accept( tmv );
            System.out.println( ain.getClass().toString() );
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        t.print( pw );
        System.out.println( sw.toString() );
        ownerClass.addMethod( name, desc );
    }
}
