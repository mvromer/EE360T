package ee360t.controlflow.trace.agent;

import java.lang.instrument.Instrumentation;

public class TracerAgent {
    public static void premain( String agentArgs, Instrumentation inst ) {
        System.out.println( "Hi from agent" );
    }
}
