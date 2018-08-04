package ee360t.controlflow.model;

import ee360t.controlflow.utility.ClassBuilder;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassRepository {
    Map<String, JavaClass> classes;

    public ClassRepository() {
        classes = new HashMap<>();
    }

    public void addClass( String className ) throws IOException {
        ClassReader classReader = new ClassReader( className );
        if( classes.containsKey( classReader.getClassName() ) )
            return;

        ClassBuilder classBuilder = new ClassBuilder();
        classReader.accept( classBuilder, 0 );
        JavaClass result = classBuilder.getResult();
        classes.put( result.getName(), result );
    }

    public Collection<JavaClass> getClasses() {
        return classes.values();
    }
}
