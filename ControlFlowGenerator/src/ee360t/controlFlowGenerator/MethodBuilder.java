package ee360t.controlFlowGenerator;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class MethodBuilder extends MethodVisitor {
    Method result;

    public MethodBuilder( String methodName, String methodDescriptor ) {
        super( ASM4 );
        result = new Method( methodName, methodDescriptor );
    }

    public Method getResult() {
        return result;
    }
}
