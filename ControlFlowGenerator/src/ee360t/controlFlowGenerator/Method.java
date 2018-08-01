package ee360t.controlFlowGenerator;

import java.util.Objects;

public class Method {
    String name;
    String descriptor;

    public Method( String name, String descriptor ) {
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o )
            return true;

        if( o == null || getClass() != o.getClass() )
            return false;

        Method method = (Method) o;
        return Objects.equals( name, method.name ) &&
            Objects.equals( descriptor, method.descriptor );
    }

    @Override
    public int hashCode() {
        return Objects.hash( name, descriptor );
    }
}
