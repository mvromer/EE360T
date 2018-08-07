package ee360t.controlflow.trace.agent;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.EnumConverter;

public class Options {
    @Parameter( names = { "-trace", "-t" }, description = "Type of trace information to produce" )
    public TraceType traceType = TraceType.All;
}
