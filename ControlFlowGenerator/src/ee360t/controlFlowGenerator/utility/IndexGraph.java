package ee360t.controlFlowGenerator.utility;

import java.io.PrintStream;
import java.util.*;

public class IndexGraph {
    Set<Integer> nodes = new HashSet<>();
    Map<Integer, Set<Integer>> edges = new HashMap<>();

    public void addNode( int iNode ) {
        nodes.add( iNode );
        edges.computeIfAbsent( iNode, k -> new HashSet<>() );
    }

    public void addEdge( int iFrom, int iTo ) {
        addNode( iFrom );
        addNode( iTo );
        Set<Integer> successors = edges.get( iFrom );
        successors.add( iTo );
    }

    public Set<Integer> getNodes() {
        return nodes;
    }

    public Map<Integer, Set<Integer>> getEdges() {
        return edges;
    }

    public void printDot( PrintStream stream ) {
        stream.println( "digraph {" );
        for( int iFrom : edges.keySet() ) {
            for( int iTo : edges.get( iFrom ) ) {
                stream.println( String.format( "    %d -> %d", iFrom, iTo ) );
            }
        }
        stream.println( "}" );
    }
}
