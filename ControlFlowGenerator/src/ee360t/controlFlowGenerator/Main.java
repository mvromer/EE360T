package ee360t.controlFlowGenerator;

public class Main {

    public static void main( String[] args ) {
        String[] exampleClasses = {
            "ee360t.controlFlowGenerator.examples.E1"
        };

        ClassRepository repository = new ClassRepository();

        try {
            for( String exampleClass : exampleClasses ) {
                repository.addClass( exampleClass );
            }

            for( String name : repository.getClasses().keySet() ) {
                System.out.println( name );
            }
        }
        catch( Exception ex ) {
            System.err.println( "Error running generator." );
            ex.printStackTrace( System.err );
        }
    }
}
