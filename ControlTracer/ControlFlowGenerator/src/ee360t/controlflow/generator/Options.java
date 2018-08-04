package ee360t.controlflow.generator;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Options {
    @Parameter( names = { "-class", "-c" }, description = "Classes to process" )
    public List<String> inputClasses = new ArrayList<>();

    @Parameter( names = { "-out", "-o" } )
    public String outputPath;
}
