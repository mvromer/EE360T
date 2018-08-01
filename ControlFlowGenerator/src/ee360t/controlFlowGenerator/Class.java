package ee360t.controlFlowGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Class {
    String name;
    String superClass;
    String[] interfaces;
    Map<MethodKey, Method> methods;

    public Class( String name, String superClass, String[] interfaces ) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.methods = new HashMap<>();
    }

    public void addMethod( String methodName, String methodDescriptor ) {
        methods.put( new MethodKey( methodName, methodDescriptor ),
            new Method( methodName, methodDescriptor ) );
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

    public Collection<Method> getMethods() {
        return methods.values();
    }

    private class MethodKey {
        public String name;
        public String descriptor;

        public MethodKey( String name, String descriptor ) {
            this.name = name;
            this.descriptor = descriptor;
        }

        @Override
        public boolean equals( Object o ) {
            if( this == o )
                return true;

            if( o == null || getClass() != o.getClass() )
                return false;

            MethodKey methodKey = (MethodKey) o;
            return Objects.equals( name, methodKey.name ) &&
                Objects.equals( descriptor, methodKey.descriptor );
        }

        @Override
        public int hashCode() {
            return Objects.hash( name, descriptor );
        }
    }
}
