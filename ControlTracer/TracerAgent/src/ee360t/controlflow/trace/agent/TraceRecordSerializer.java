package ee360t.controlflow.trace.agent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TraceRecordSerializer implements JsonSerializer<TraceRecord> {
    @Override
    public JsonElement serialize( TraceRecord traceRecord, Type type, JsonSerializationContext context ) {
        JsonObject json = new JsonObject();
        json.addProperty( "label", traceRecord.getLabel() );
        json.addProperty( "testClassName", traceRecord.getTestClassName() );
        json.addProperty( "testMethodName", traceRecord.getTestMethodName() );
        json.addProperty( "testMethodDescriptor", traceRecord.getTestMethodDescriptor() );
        json.add( "callEdges", context.serialize( traceRecord.getCallEdges() ) );
        json.add( "tracePath", context.serialize( traceRecord.getTracePath() ) );
        return json;
    }
}
