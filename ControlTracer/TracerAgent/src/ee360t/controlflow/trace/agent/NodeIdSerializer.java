package ee360t.controlflow.trace.agent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ee360t.controlflow.model.NodeId;

import java.lang.reflect.Type;

public class NodeIdSerializer implements JsonSerializer<NodeId> {
    @Override
    public JsonElement serialize( NodeId nodeId, Type type, JsonSerializationContext jsonSerializationContext ) {
        String[] classNameParts = nodeId.getClassName().split( "/" );
        String className = classNameParts[classNameParts.length - 1];
        JsonObject json = new JsonObject();

        json.addProperty( "classInternalName", nodeId.getClassName() );
        json.addProperty( "className", className );
        json.addProperty( "methodName", nodeId.getMethodName() );
        json.addProperty( "methodDescriptor", nodeId.getMethodDescriptor() );
        json.addProperty( "localId", nodeId.getLocalId() );
        return json;
    }
}
