package ee360t.controlflow.model;

import ee360t.controlflow.utility.IndexGraph;

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
