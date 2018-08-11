package ee360t.controlflow.model;

import ee360t.controlflow.utility.ControlFlow;

public class Method {
    String name;
    String descriptor;
    ControlFlow controlFlow;

    public Method( String name, String descriptor, ControlFlow controlFlow ) {
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

    public ControlFlow getControlFlow() {
        return controlFlow;
    }
}
