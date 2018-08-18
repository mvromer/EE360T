package ee360t.controlflow.trace.examples;

import ee360t.controlflow.trace.examples.scc.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class E6 {
    public static void M1() {
        List<Object> data  = new ArrayList<>();
        data.add( 0 );
        data.add( 1 );
        data.add( 2 );
        data.add( 3 );
        Graph g = new Graph( data );
        g.addEdge( 0, 1 );
        g.addEdge( 0, 2 );

        System.out.println( "Graph representation" );
        g.printGraph();
        System.out.println();

        List<List<Object>> sccs = g.getStronglyConnectedComponents();
        System.out.println( "Strongly connected components" );
        for( List<Object> scc : sccs ) {
            System.out.println( Arrays.toString( scc.toArray() ) );
        }
    }
}
