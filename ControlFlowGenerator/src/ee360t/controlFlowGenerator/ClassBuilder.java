package ee360t.controlFlowGenerator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassBuilder extends ClassVisitor {
    Class m_class;

    public ClassBuilder() {
        super( ASM4 );
    }

    // Pre: The builder must have visited some Java class file using ASM's ClassReader.
    public Class getResult() {
        if( m_class == null )
            throw new RuntimeException( "Result class not built." );
        return m_class;
    }

    @Override
    public void visit( int version, int access, String name, String signature, String superName, String[] interfaces ) {
        m_class = new Class( name, superName, interfaces );
    }

    @Override
    public MethodVisitor visitMethod( int access, String name, String descriptor, String signature, String[] exceptions ) {
        System.out.println( "Method name: " + name );
        System.out.println( "Method descriptor: " + descriptor );
        System.out.println( "Method signature: " + signature );
        System.out.println( "Method throws: " + (exceptions != null ? String.join( ", ", exceptions ) : "null") );
        System.out.println();
        return null;
    }
}
