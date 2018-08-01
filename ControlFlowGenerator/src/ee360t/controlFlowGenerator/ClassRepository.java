package ee360t.controlFlowGenerator;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClassRepository {
    Map<String, Class> classes;

    public ClassRepository() {
        classes = new HashMap<>();
    }

    public void addClass( String className ) throws IOException {
        ClassReader classReader = new ClassReader( className );
        if( classes.containsKey( classReader.getClassName() ) )
            return;

        ClassBuilder classBuilder = new ClassBuilder();
        classReader.accept( classBuilder, 0 );
        Class result = classBuilder.getResult();
        classes.put( result.getName(), result );
    }

    public Map<String, Class> getClasses() {
        return classes;
    }
}
