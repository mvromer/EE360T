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
                .registerTypeAdapter( NodeId.class, new NodeIdSerializer() )
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        JsonObject results = new JsonObject();
        //results.add( "globalIdToNodeId", gson.toJsonTree( nodeIds ) );
        results.add( "traceRecords", gson.toJsonTree( traceRecords ) );

        JsonObject controlFlowsJson = new JsonObject();
        Map<String, JsonObject> controlFlowsForClass = new HashMap<>();

        int nextMethodId = 0;
        for( MethodId methodId : controlFlows.keySet() ) {
            IndexGraph controlFlow = controlFlows.get( methodId );
            String className = methodId.getClassName();
            JsonObject controlFlowJson = controlFlowsForClass.computeIfAbsent( className,
                trash -> {
                    JsonObject json = new JsonObject();
                    String[] classNameParts = methodId.getClassName().split( "/" );
                    String displayName = classNameParts[classNameParts.length - 1];

                    controlFlowsJson.add( className, json );
                    json.addProperty( "classDisplayName", displayName );
                    json.add( "methods", new JsonArray() );
                    return json;
                } );

            JsonArray methodsJson = controlFlowJson.getAsJsonArray( "methods" );
            JsonObject methodJson = new JsonObject();
            methodJson.addProperty( "methodName", methodId.getMethodName() );
            methodJson.addProperty( "methodDescriptor", methodId.getMethodDescriptor() );
            methodJson.addProperty(  "methodId", nextMethodId );
            methodJson.add( "nodes", gson.toJsonTree( controlFlow.getNodes().stream().mapToInt( localId ->  {
                NodeId nodeId = new NodeId( className, methodId.getMethodName(),
                    methodId.getMethodDescriptor(), localId  );
                return globalIds.get( nodeId );
            } ).toArray() ) );

            Map<Integer, Set<Integer>> edges = controlFlow.getEdges();
            Map<Integer, Set<Integer>> mappedEdges = new HashMap<>();
            for( int iFrom : edges.keySet() ) {
                NodeId fromNodeId = new NodeId( className, methodId.getMethodName(),
                    methodId.getMethodDescriptor(), iFrom );
                Set<Integer> mappedTo = new HashSet<>();

                for( int iTo : edges.get( iFrom ) ) {
                    NodeId toNodeId = new NodeId( className, methodId.getMethodName(),
                        methodId.getMethodDescriptor(), iTo );
                    mappedTo.add( globalIds.get( toNodeId ) );
                }

                mappedEdges.put( globalIds.get( fromNodeId ), mappedTo );
            }
           methodJson.add( "edges", gson.toJsonTree( mappedEdges ) );

            ++nextMethodId;
            methodsJson.add( methodJson );
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
