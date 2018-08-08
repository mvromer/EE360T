package ee360t.controlflow.trace.agent;

import java.util.*;

public class TraceRegistry {
    private static Map<NodeId, Integer> globalIds = new HashMap<>();
    private static Map<Integer, NodeId> methodIds = new HashMap<>();
    private static List<TraceRecord> traceRecords = new ArrayList<>();
    private static TraceRecord currentRecord;

    public static void startNewTrace( String testClassName, String testMethodName, String testMethodDescriptor ) {
        System.out.println( "New trace started for " + testClassName + "." + testMethodName + testMethodDescriptor );
        currentRecord = new TraceRecord( testClassName, testMethodName, testMethodDescriptor );
        traceRecords.add( currentRecord );
    }

    public static void visitNode( int globalId ) {
        if( currentRecord == null )
            throw new RuntimeException( "New trace record must be started before visiting any nodes." );
        System.out.println( "Visited node with global ID: " + globalId );
        currentRecord.addNode( globalId );
    }

    public static int getGlobalId( String className, String methodName, String methodDescriptor, int localId ) {
        NodeId nodeId = new NodeId( className, methodName, methodDescriptor, localId );
        return globalIds.computeIfAbsent( nodeId, key -> {
            int globalId = globalIds.size();
            methodIds.put( globalId, key );
            return globalId;
        } );
    }

    private static class NodeId {
        public String className;
        public String methodName;
        public String methodDescriptor;
        public int localId;

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

    private TraceRegistry() {}
}
