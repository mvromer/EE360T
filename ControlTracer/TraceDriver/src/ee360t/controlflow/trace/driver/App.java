package ee360t.controlflow.trace.driver;

import ee360t.controlflow.trace.examples.E1;
import ee360t.controlflow.trace.examples.E3;
import org.junit.Test;

public class App {
    @Test
    public void t1() {
        E1.M1();
    }

    @Test
    public void t2() {
        E1.M2();
    }

    @Test
    public void t3() { E1.M3(); }

    @Test
    public void t4() { E3.M1(); }

    @Test
    public void t5() { E3.M3(); }
}
