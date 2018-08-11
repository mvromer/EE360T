package ee360t.controlflow.trace.examples;

public class E3 {
    public static void M1() {
        M2();
    }

    public static void M2() {}

    public static void M3() {
        M2();
    }
}
