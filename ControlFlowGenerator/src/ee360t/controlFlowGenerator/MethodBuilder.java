package ee360t.controlFlowGenerator;

import org.objectweb.asm.tree.MethodNode;

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
        // TODO: Build CFG using Analyzer.
        ownerClass.addMethod( name, desc );
    }
}
