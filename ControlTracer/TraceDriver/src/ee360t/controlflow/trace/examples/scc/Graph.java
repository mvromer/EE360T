package ee360t.controlflow.trace.examples.scc;

import java.util.*;
import java.util.stream.Collectors;

import static ee360t.controlflow.trace.examples.scc.Vertex.INVALID_INDEX;

public class Graph {
    Set<Vertex> vertices = new HashSet<>();
    Map<Vertex, Set<Vertex>> edges = new HashMap<>();

    public Graph( List<Object> data ) {
        for( Object datum : data ) {
            addVertex( datum );
        }
    }

    public void addVertex( Object data ) {
        Vertex v = new Vertex( data );
        if( vertices.contains( v ) ) {
            throw new RuntimeException( "Identical data attempting to be inserted into graph as two separate vertices" );
        }

        edges.put( v, new HashSet<>() );
        vertices.add( v );
    }

    public void addEdge( Object fromData, Object toData ) {
        Vertex fromVert = new Vertex( fromData );
        Vertex toVert = new Vertex( toData );

        if( !vertices.contains( fromVert ) ) {
            throw new RuntimeException( "Failed to add edge because from data does not map to an existing vertex." );
        }

        if( !vertices.contains( toVert ) ) {
            throw new RuntimeException( "Failed to add edge because to data does not map to an existing vertex." );
        }

        Set<Vertex> adjacencyList = edges.get( fromVert );

        if( adjacencyList.contains( toVert ) ) {
            throw new RuntimeException( "Failed to add edge to graph because an edge already exists from from data to to data" );
        }

        adjacencyList.add( toVert );
    }

    private class SCCWorkspace {
        int index = 0;
        List<List<Object>> sccs = new ArrayList<>();
        Stack<Vertex> vertexStack = new Stack<>();
    }

    private void visitVertex( Vertex vertex, SCCWorkspace workspace ) {
        vertex.index = workspace.index;
        vertex.lowlink = workspace.index;
        vertex.visited = true;
        workspace.index += 1;

        workspace.vertexStack.push( vertex );

        for( Vertex vertexPrime : edges.get( vertex ) ) {
            if( vertexPrime.index == INVALID_INDEX ) {
                visitVertex( vertexPrime, workspace );
                vertex.lowlink = Math.min( vertex.lowlink, vertexPrime.lowlink );
            }
            else if( vertexPrime.visited ) {
                vertex.lowlink = Math.min( vertex.lowlink, vertexPrime.index );
            }
        }

        if( vertex.lowlink == vertex.index ) {
            List<Object> scc = new ArrayList<>();
            while( true ) {
                Vertex vertexPrime = workspace.vertexStack.pop();
                scc.add( vertexPrime.data );
                if( vertexPrime.equals( vertex ) ) {
                    workspace.sccs.add( scc );
                    break;
                }
            }
        }
    }

    public List<List<Object>> getStronglyConnectedComponents() {
        SCCWorkspace workspace = new SCCWorkspace();

        for( Vertex vertex : vertices ) {
            vertex.reset();
        }

        for( Vertex vertex : vertices ) {
            if( vertex.index == INVALID_INDEX ) {
                visitVertex( vertex, workspace );
            }
        }

        return workspace.sccs;
    }

    public List<List<Object>> getCircularDependencies() {
        List<List<Object>> sccs = getStronglyConnectedComponents();
        return sccs.stream().filter( scc -> scc.size() > 1 ).collect( Collectors.toList() );
    }

    public List<Object> getSelfLoops() {
        List<Object> selfLoops = new ArrayList<>();

        for( Vertex fromVert : edges.keySet() ) {
            Set<Vertex> adjacencyList = edges.get( fromVert );
            for( Vertex toVert : adjacencyList ) {
                if( fromVert.equals( toVert ) ) {
                    selfLoops.add( fromVert.data );
                }
            }
        }

        return selfLoops;
    }

    public boolean isDag() {
        List<List<Object>> circularDependencies = getCircularDependencies();
        List<Object> selfLoops = getSelfLoops();
        return circularDependencies.isEmpty() && selfLoops.isEmpty();
    }

    public void printGraph() {
        List<String> vertexStrings = new ArrayList<>();
        for( Vertex vertex : vertices ) {
            vertexStrings.add( vertex.toString() );
        }

        List<String> edgeStrings = new ArrayList<>();
        for( Vertex fromVert : edges.keySet() ) {
            Set<Vertex> adjacencyList = edges.get( fromVert );
            for( Vertex toVert : adjacencyList ) {
                edgeStrings.add( "(" + fromVert.toString() + ", " + toVert.toString() + ")" );
            }
        }

        System.out.println( "Vertices: [" + String.join( ", ", vertexStrings ) + "]" );
        System.out.println( "Edges: [" + String.join( ", ", edgeStrings ) + "]" );
    }
}
