package ee360t.controlFlowGenerator.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ee360t.controlFlowGenerator.Class;
import ee360t.controlFlowGenerator.ClassRepository;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassRepositorySerializer implements JsonSerializer<ClassRepository> {
    @Override
    public JsonElement serialize( ClassRepository repository, Type type, JsonSerializationContext context ) {
        Map<String, Class> classes = new HashMap<>();
        for( Class clazz : repository.getClasses() ) {
            classes.put( clazz.getName(), clazz );
        }

        JsonObject json = new JsonObject();
        json.add( "classes", context.serialize( classes ) );
        return json;
    }
}
