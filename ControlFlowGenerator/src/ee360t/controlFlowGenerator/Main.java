package ee360t.controlFlowGenerator;

public class Main {

    public static void main( String[] args ) {
        String[] exampleClasses = {
            "ee360t.controlFlowGenerator.examples.E3"
        };

        ClassRepository repository = new ClassRepository();

        try {
            for( String exampleClass : exampleClasses ) {
                repository.addClass( exampleClass );
            }

            for( Class clazz : repository.getClasses() ) {
                System.out.println( "Class: " + clazz.getName() );
                System.out.println( "Methods:" );
                for( Method method : clazz.getMethods() ) {
                    System.out.println( "    " + method.getName() + method.getDescriptor() );
                }
                System.out.println();
            }
        }
        catch( Exception ex ) {
            System.err.println( "Error running generator." );
            ex.printStackTrace( System.err );
        }
    }
}
