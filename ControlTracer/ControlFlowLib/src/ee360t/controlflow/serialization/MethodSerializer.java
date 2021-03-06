package ee360t.controlflow.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ee360t.controlflow.model.Method;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodSerializer implements JsonSerializer<Method> {
    @Override
    public JsonElement serialize( Method method, Type type, JsonSerializationContext context ) {
        JsonObject json = new JsonObject();
        json.add( "cfgNodes", context.serialize( method.getControlFlow().getNodes() ) );

        List<int[]> cfgEdges = new ArrayList<>();
        Map<Integer, Set<Integer>> nodeEdges = method.getControlFlow().getNodeEdges();

        for( int iFrom : nodeEdges.keySet() ) {
            Set<Integer> successors = nodeEdges.get( iFrom );
            for( int iTo : successors ) {
                cfgEdges.add( new int[] { iFrom, iTo } );
            }
        }

        json.add( "cfgEdges", context.serialize( cfgEdges ) );
        return json;
    }
}
