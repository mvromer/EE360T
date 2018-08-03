package ee360t.controlFlowGenerator.utility;

import java.io.PrintStream;
import java.util.*;

public class IndexGraph {
    Set<Integer> nodes = new HashSet<>();
    Map<Integer, Set<Integer>> edges = new HashMap<>();
    Map<Integer, Set<Integer>> backEdges = new HashMap<>();

    public void addNode( int iNode ) {
        nodes.add( iNode );
        edges.computeIfAbsent( iNode, k -> new HashSet<>() );
        backEdges.computeIfAbsent( iNode, k -> new HashSet<>() );
    }

    public void removeNode( int iNode ) {
        if( nodes.contains( iNode ) ) {
            Set<Integer> predecessors = backEdges.get( iNode );
            Set<Integer> successors = edges.get( iNode );

            for( int iSuccessor : successors ) {
                // Remove the back edge to the given node from its successor.
                backEdges.get( iSuccessor ).remove( iNode );
            }

            for( int iPredecessor : predecessors ) {
                // Remove the forward edge to the given node from its predecessor.
                edges.get( iPredecessor ).remove( iNode );

                // Add an edge from the predecessor to each of the given node's successors.
                for( int iSuccessor : successors ) {
                    addEdge( iPredecessor, iSuccessor );
                }
            }

            // Remove the forward edges from the given node to all of its successors.
            // Remove the back edges from the given node to all of its predecessors.
            // Remove the given node from the set of nodes.
            edges.remove( iNode );
            backEdges.remove( iNode );
            nodes.remove( iNode );
        }
    }

    public void addEdge( int iFrom, int iTo ) {
        addNode( iFrom );
        addNode( iTo );
        edges.get( iFrom ).add( iTo );
        backEdges.get( iTo ).add( iFrom );
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
