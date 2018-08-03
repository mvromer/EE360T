package ee360t.controlFlowGenerator;

import ee360t.controlFlowGenerator.utility.ClassBuilder;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.Collection;
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

    public Collection<Class> getClasses() {
        return classes.values();
    }
}
