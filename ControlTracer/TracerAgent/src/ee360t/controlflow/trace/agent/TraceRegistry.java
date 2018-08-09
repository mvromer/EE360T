package ee360t.controlflow.trace.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ee360t.controlflow.utility.IndexGraph;

import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class TraceRegistry {
    private static Map<MethodId, IndexGraph> controlFlows = new HashMap<>();
    private static Map<NodeId, Integer> globalIds = new HashMap<>();
    private static Map<Integer, NodeId> nodeIds = new HashMap<>();
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
            nodeIds.put( globalId, key );
            return globalId;
        } );
    }

    public static void setControlFlow( IndexGraph controlFlow, String className, String methodName, String methodDescriptor ) {
        MethodId methodId = new MethodId( className, methodName, methodDescriptor );
        controlFlows.put( methodId, controlFlow );
    }

    public static void serialize( String outputPath ) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter( TraceRecord.class, new TraceRecordSerializer() )
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        JsonObject results = new JsonObject();
        results.add( "globalIdToNodeId", gson.toJsonTree( nodeIds ) );
        results.add( "traceRecords", gson.toJsonTree( traceRecords ) );

        JsonArray controlFlowsJson = new JsonArray();
        for( MethodId methodId : controlFlows.keySet() ) {
            IndexGraph controlFlow = controlFlows.get( methodId );
            JsonObject controlFlowJson = new JsonObject();

            controlFlowJson.addProperty( "className", methodId.getClassName() );
            controlFlowJson.addProperty( "methodName", methodId.getMethodName() );
            controlFlowJson.addProperty( "methodDescriptor", methodId.getMethodDescriptor() );
            controlFlowJson.add( "nodes", gson.toJsonTree( controlFlow.getNodes() ) );
            controlFlowJson.add( "edges", gson.toJsonTree( controlFlow.getEdges() ) );
            controlFlowsJson.add( controlFlowJson );
        }

        results.add( "controlFlows", controlFlowsJson );

        try( Writer outputWriter = new FileWriter( outputPath ) ) {
            outputWriter.write( gson.toJson( results ) );
        }
        catch( Exception ex ) {
            System.err.println( "Error writing trace results." );
            ex.printStackTrace( System.err );
        }
    }

    private TraceRegistry() {}
}
