package ee360t.controlflow.trace.examples;

public class E1 {
    public static void M1() {}

    public static int M2() {
        int x = 5;
        if( x < 0 )
            x = x + 10;
        return x;
    }

    public static void M3() {
        E2.M1();
    }
}
