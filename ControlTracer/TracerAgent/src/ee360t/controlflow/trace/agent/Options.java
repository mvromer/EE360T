package ee360t.controlflow.trace.agent;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Options {
    @Parameter( names = { "-trace", "-t" },
        description = "Type of trace information to produce" )
    public TraceType traceType = TraceType.All;

    @Parameter( names = { "-prefix", "-p" },
        description = "Prefixes of fully qualified class names to trace" )
    public List<String> prefixesToTrace = new ArrayList<>();

    @Parameter( names = { "-verbose", "-v" } )
    public boolean verbose = false;
}
