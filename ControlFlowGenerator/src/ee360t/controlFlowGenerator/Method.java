package ee360t.controlFlowGenerator;

public class Method {
    String name;
    String descriptor;

    public Method( String name, String descriptor ) {
        this.name = name;
        this.descriptor = descriptor;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
