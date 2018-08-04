package ee360t.controlFlowGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee360t.controlFlowGenerator.serialization.ClassRepositorySerializer;
import ee360t.controlFlowGenerator.serialization.ClassSerializer;
import ee360t.controlFlowGenerator.serialization.MethodSerializer;
import ee360t.controlFlowGenerator.utility.IndexGraph;

import java.util.Map;
import java.util.Set;

public class Main {

    public static void main( String[] args ) {
        String[] exampleClasses = {
            "ee360t.controlFlowGenerator.examples.E1",
            "ee360t.controlFlowGenerator.examples.E2",
            "ee360t.controlFlowGenerator.examples.E3"
        };

        ClassRepository repository = new ClassRepository();

        try {
            for( String exampleClass : exampleClasses ) {
                repository.addClass( exampleClass );
            }

            for( Class clazz : repository.getClasses() ) {
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

            // Serialize.
            Gson gson = new GsonBuilder()
                .registerTypeAdapter( ClassRepository.class, new ClassRepositorySerializer() )
                .registerTypeAdapter( Class.class, new ClassSerializer() )
                .registerTypeAdapter( Method.class, new MethodSerializer() )
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
            String json = gson.toJson( repository );
            System.out.println( json );
        }
        catch( Exception ex ) {
            System.err.println( "Error running generator." );
            ex.printStackTrace( System.err );
        }
    }
}
