package ee360t.controlFlowGenerator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassBuilder extends ClassVisitor {
    Class result;

    public ClassBuilder() {
        super( ASM4 );
    }

    // Pre: The builder must have visited some Java class file using ASM's ClassReader.
    public Class getResult() {
        if( result == null )
            throw new RuntimeException( "Result class not built." );
        return result;
    }

    @Override
    public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
        result = new Class( name, superName, interfaces );
    }

    @Override
    public MethodVisitor visitMethod( int access, String name, String descriptor, String signature, String[] exceptions ) {
        MethodBuilder builder = new MethodBuilder( result, access, name, descriptor, signature, exceptions );
        return builder;
    }
}
