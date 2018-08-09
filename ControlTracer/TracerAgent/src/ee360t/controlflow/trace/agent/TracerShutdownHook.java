package ee360t.controlflow.trace.agent;

import com.google.gson.Gson;

public class TracerShutdownHook extends Thread {
    private String outputPath;

    public TracerShutdownHook( String outputPath ) {
        this.outputPath = outputPath;
    }

    @Override
    public void run() {
        if( outputPath != null ) {
            TraceRegistry.serialize( outputPath );
        }
    }
}
