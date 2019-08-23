package ee360t.controlflow.trace.examples;

import ee360t.controlflow.trace.examples.scc.Graph;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class E5 {
    public static void M1( int x ) {
        try {
            if( x == 0 )
                throw new RuntimeException( "Thrown exception" );
        }
        catch( Exception ex ) {
            System.out.println( "Exception caught" );
            return;
        }
        System.out.println( "Success" );
    }

    public static void M2( int x ) {
        try {
            int y = 1 / x;
        }
        catch( Exception ex ) {
            System.out.println( "Exception caught" );
            return;
        }
        System.out.println( "Success" );
    }

    public static void M3( int x ) {
        if( x == 0 )
            return;

        E5.M3( x - 1 );
    }

    public static void M4() {}

    public static void M5() {
        B1 b1 = new D1();
        b1.Foo();
    }

    public static int M6( int x, int runningTotal ) {
        if( x == 0 ) {
            return runningTotal;
        }

        return M6( x - 1, runningTotal + x );
    }
}
