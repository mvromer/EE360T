package ee360t.controlflow.trace.agent;

import java.util.Objects;

class NodeId {
    private String className;
    private String methodName;
    private String methodDescriptor;
    private int localId;

    public NodeId( String className, String methodName, String methodDescriptor, int localId ) {
        this.className = className;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.localId = localId;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o )
            return true;

        if( o == null || getClass() != o.getClass() )
            return false;

        NodeId nodeId = (NodeId) o;
        return Objects.equals( className, nodeId.className ) &&
                Objects.equals( methodName, nodeId.methodName ) &&
                Objects.equals( methodDescriptor, nodeId.methodDescriptor ) &&
                localId == nodeId.localId;
    }

    @Override
    public int hashCode() {
        return Objects.hash( className, methodName, methodDescriptor, localId );
    }
}
