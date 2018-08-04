package ee360t.controlflow.generator;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee360t.controlflow.model.ClassRepository;
import ee360t.controlflow.model.JavaClass;
import ee360t.controlflow.model.Method;
import ee360t.controlflow.serialization.ClassRepositorySerializer;
import ee360t.controlflow.serialization.ClassSerializer;
import ee360t.controlflow.serialization.MethodSerializer;
import ee360t.controlflow.utility.IndexGraph;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class App {

    public static void main( String[] args ) {
        String[] exampleClasses = {
            "ee360t.controlflow.generator.examples.E1",
            "ee360t.controlflow.generator.examples.E2",
            "ee360t.controlflow.generator.examples.E3"
        };

        try {
            // Parse command line options.
            Options options = new Options();
            JCommander.newBuilder()
                .addObject( options )
                .build()
                .parse( args );

            if( options.inputClasses.isEmpty() )
                options.inputClasses.addAll( Arrays.asList( exampleClasses ) );

            // Initialize the class repository.
            ClassRepository repository = new ClassRepository();
            for( String exampleClass : exampleClasses ) {
                repository.addClass( exampleClass );
            }

            // Output information on each class.
            for( JavaClass clazz : repository.getClasses() ) {
                System.out.println( "Class: " + clazz.getName() );
                System.out.println( "    Methods:" );
                for( Method method : clazz.getMethods() ) {
                    System.out.println( "        " + method.getName() + method.getDescriptor() );
                    System.out.println( "            Control Flow:" );

                    IndexGraph controlFlow = method.getControlFlow();
                    Map<Integer, Set<Integer>> edges = controlFlow.getEdges();
                    for( int iFrom : edges.keySet() ) {
                        for( int iTo : edges.get( iFrom ) ) {
                            System.out.println( String.format( "                %d -> %d", iFrom, iTo ) );
                        }
                    }
                }
                System.out.println();
            }

            // Serialize the results to JSON.
            Gson gson = new GsonBuilder()
                .registerTypeAdapter( ClassRepository.class, new ClassRepositorySerializer() )
                .registerTypeAdapter( Class.class, new ClassSerializer() )
                .registerTypeAdapter( Method.class, new MethodSerializer() )
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
            String json = gson.toJson( repository );
            System.out.println( json );

            if( options.outputPath != null ) {
                try( Writer outputWriter = new FileWriter( options.outputPath ) ) {
                    outputWriter.write( json );
                }
            }
        }
        catch( Exception ex ) {
            System.err.println( "Error running generator." );
            ex.printStackTrace( System.err );
        }
    }
}
