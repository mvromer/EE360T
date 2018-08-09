package ee360t.controlflow.trace.agent;

import java.util.Objects;

public class MethodId {
    private String className;
    private String methodName;
    private String methodDescriptor;

    public MethodId( String className, String methodName, String methodDescriptor ) {
        this.className = className;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    String getClassName() {
        return className;
    }

    String getMethodName() {
        return methodName;
    }

    String getMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o )
            return true;

        if( o == null || getClass() != o.getClass() )
            return false;

        MethodId methodId = (MethodId) o;
        return Objects.equals( className, methodId.className ) &&
                Objects.equals( methodName, methodId.methodName ) &&
                Objects.equals( methodDescriptor, methodId.methodDescriptor );
    }

    @Override
    public int hashCode() {
        return Objects.hash( className, methodName, methodDescriptor );
    }
}
