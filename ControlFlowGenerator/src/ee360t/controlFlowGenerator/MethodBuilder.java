package ee360t.controlFlowGenerator;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

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
        // TODO: Build CFG using Analyzer.'
        Analyzer<BasicValue> a = new Analyzer<BasicValue>( new BasicInterpreter() ) {
            @Override
            protected void newControlFlowEdge( int insnIndex, int successorIndex ) {
                System.out.println( "edge from " + Integer.toString( insnIndex ) + " to " + Integer.toString( successorIndex ) );
            }
        };
        try {
            a.analyze( ownerClass.getName(), this );
        }
        catch( AnalyzerException ex ) {
            // do nothing.
            System.err.println( "Analyzer error" );
            ex.printStackTrace( System.err );
        }
        System.out.println();
        ownerClass.addMethod( name, desc );
    }
}
