package ee360t.controlflow.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ee360t.controlflow.model.JavaClass;
import ee360t.controlflow.model.Method;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassSerializer implements JsonSerializer<JavaClass> {
    @Override
    public JsonElement serialize( JavaClass clazz, Type type, JsonSerializationContext context ) {
        Map<String, Method> methods = new HashMap<>();
        for( Method method : clazz.getMethods() ) {
            methods.put( method.getName() + method.getDescriptor(), method );
        }

        JsonObject json = new JsonObject();
        json.add( "methods", context.serialize( methods ) );
        return json;
    }
}
