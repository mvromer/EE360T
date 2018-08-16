package ee360t.controlflow.trace.agent;

import com.beust.jcommander.JCommander;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class TracerAgent {
    public static void premain( String agentArgs, Instrumentation inst ) {
        Options options = new Options();
        if( agentArgs != null ) {
            JCommander.newBuilder()
                .addObject( options )
                .build()
                .parse( agentArgs.split( "\\s" ) );
        }

        // Append current directory to list of source paths.
        options.sourcePaths.add( "." );

        TraceRegistry.createDefaultTrace();
        Runtime.getRuntime().addShutdownHook( new TracerShutdownHook( options.outputPath, options.sourcePaths ) );
        inst.addTransformer( new TraceTransformer( options.prefixesToTrace, options.verbose ) );
    }
}
