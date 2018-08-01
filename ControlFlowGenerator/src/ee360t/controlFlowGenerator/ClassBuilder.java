package ee360t.controlFlowGenerator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

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
        //MethodBuilder builder = new MethodBuilder( name, descriptor );
        System.out.println( "Method name: " + name );
        System.out.println( "Method descriptor: " + descriptor );
        System.out.println( "Method signature: " + signature );
        System.out.println( "Method throws: " + (exceptions != null ? String.join( ", ", exceptions ) : "null") );

        return new MethodNode( ASM4, access, name, descriptor, signature, exceptions ) {
            @Override
            public void visitEnd() {
                // TODO: Build CFG using Analyzer.
                System.out.println();
            }
        };
    }
}
