package ee360t.controlflow.trace.agent;

import com.beust.jcommander.JCommander;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class TracerAgent {
    public static void premain( String agentArgs, Instrumentation inst ) {
        // TODO: This will likely move into the transformer.
        Runtime.getRuntime().addShutdownHook( new TracerShutdownHook() );

        Options options = new Options();
        if( agentArgs != null ) {
            JCommander.newBuilder()
                .addObject( options )
                .build()
                .parse( agentArgs.split( "\\s" ) );
        }

        System.out.println( "Trace type: " + options.traceType.toString() );
        System.out.println( "Prefixes: " + Arrays.toString( options.prefixesToTrace.toArray() ) + "\n" );
        inst.addTransformer( new TraceTransformer( options.prefixesToTrace, options.verbose ) );
    }
}
