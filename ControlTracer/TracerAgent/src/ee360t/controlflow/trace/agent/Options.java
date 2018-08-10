package ee360t.controlflow.trace.agent;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Options {
    @Parameter( names = { "-prefix", "-p" },
        description = "Prefixes of fully qualified class names to trace" )
    public List<String> prefixesToTrace = new ArrayList<>();

    @Parameter( names = { "-verbose", "-v" } )
    public boolean verbose = false;

    @Parameter( names = { "-out", "-o" } )
    public String outputPath;

    @Parameter( names = { "-sourcepath", "-s" },
        description = "Directories to search for source code of classes to trace" )
    public List<String> sourcePaths = new ArrayList<>();
}
