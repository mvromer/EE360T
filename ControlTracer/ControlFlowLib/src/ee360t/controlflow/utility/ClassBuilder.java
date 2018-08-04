package ee360t.controlflow.utility;

import ee360t.controlflow.model.JavaClass;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassBuilder extends ClassVisitor {
    JavaClass result;

    public ClassBuilder() {
        super( ASM4 );
    }

    // Pre: The builder must have visited some Java class file using ASM's ClassReader.
    public JavaClass getResult() {
        if( result == null )
            throw new RuntimeException( "Result class not built." );
        return result;
    }

    @Override
    public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
        result = new JavaClass( name, superName, interfaces );
    }

    @Override
    public MethodVisitor visitMethod( int access, String name, String descriptor, String signature, String[] exceptions ) {
        MethodBuilder builder = new MethodBuilder( result, access, name, descriptor, signature, exceptions );
        return builder;
    }
}
