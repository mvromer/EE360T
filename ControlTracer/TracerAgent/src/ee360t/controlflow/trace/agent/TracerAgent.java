package ee360t.controlflow.trace.agent;

import com.beust.jcommander.JCommander;

import java.lang.instrument.Instrumentation;

public class TracerAgent {
    public static void premain( String agentArgs, Instrumentation inst ) {
        Runtime.getRuntime().addShutdownHook( new TracerShutdownHook() );
        System.out.println( "Hi from agent" );

        Options options = new Options();
        if( agentArgs != null ) {
            JCommander.newBuilder()
                .addObject( options )
                .build()
                .parse( agentArgs.split( "\\s" ) );
        }

        System.out.println( "Trace type: " + options.traceType.toString() );
    }
}
