package ee360t.controlFlowGenerator;

public class Class {
    String name;
    String superClass;
    String[] interfaces;

    public Class( String name, String superClass, String[] interfaces ) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }
}
