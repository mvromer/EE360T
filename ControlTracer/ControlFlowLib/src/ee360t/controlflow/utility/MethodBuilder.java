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
        ownerClass.addMethod( name, desc, ControlFlowAnalyzer.buildControlFlow( ownerClass.getName(), this ) );
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
