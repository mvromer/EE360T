package ee360t.controlflow.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ee360t.controlflow.model.JavaClass;
import ee360t.controlflow.model.ClassRepository;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassRepositorySerializer implements JsonSerializer<ClassRepository> {
    @Override
    public JsonElement serialize( ClassRepository repository, Type type, JsonSerializationContext context ) {
        Map<String, JavaClass> classes = new HashMap<>();
        for( JavaClass clazz : repository.getClasses() ) {
            classes.put( clazz.getName(), clazz );
        }

        JsonObject json = new JsonObject();
        json.add( "classes", context.serialize( classes ) );
        return json;
    }
}
