package ee360t.controlflow.trace.agent;

import com.google.gson.Gson;

import java.util.List;

public class TracerShutdownHook extends Thread {
    private String outputPath;
    private List<String> sourcePaths;

    public TracerShutdownHook( String outputPath, List<String> sourcePaths ) {
        this.outputPath = outputPath;
        this.sourcePaths = sourcePaths;
    }

    @Override
    public void run() {
        if( outputPath != null ) {
            TraceRegistry.serialize( outputPath, sourcePaths );
        }
    }
}
