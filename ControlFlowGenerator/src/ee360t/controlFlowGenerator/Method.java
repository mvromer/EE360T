package ee360t.controlFlowGenerator;

import ee360t.controlFlowGenerator.utility.IndexGraph;

public class Method {
    String name;
    String descriptor;
    IndexGraph controlFlow;

    public Method( String name, String descriptor, IndexGraph controlFlow ) {
        this.name = name;
        this.descriptor = descriptor;
        this.controlFlow = controlFlow;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public IndexGraph getControlFlow() {
        return controlFlow;
    }
}
