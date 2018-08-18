package ee360t.controlflow.trace.examples.scc;

import java.util.Objects;
import java.util.Optional;

public class Vertex {
    final static int INVALID_INDEX = -1;

    Object data;
    int index;
    int lowlink;
    boolean visited;

    public Vertex( Object data ) {
        this.data = data;
        reset();
    }

    public void reset() {
        index = INVALID_INDEX;
        lowlink = INVALID_INDEX;
        visited = false;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals( data, vertex.data );
    }

    @Override
    public int hashCode() {
        return Objects.hash( data );
    }
}
