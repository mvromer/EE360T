package ee360t.controlflow.trace.examples;

public class E4 {
    public static void M1() {
        int x = 5;
        try {
            if( x < 0 )
                throw new RuntimeException( "No negatives" );
        }
        catch( Exception ex ) {
            ex.printStackTrace( System.out );
        }
    }
}
