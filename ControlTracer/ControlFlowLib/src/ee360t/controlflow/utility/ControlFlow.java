package ee360t.controlflow.utility;

import java.io.PrintStream;
import java.util.*;

public class ControlFlow {
    // Well-defined entry and exit nodes.
    public static final int ENTRY = -2;
    public static final int EXIT = -1;

    // Well-defined line numbers.
    public static final int INVALID_LINE = -1;

    Set<Integer> nodes = new HashSet<>();
    Map<Integer, Set<Integer>> nodeEdges = new HashMap<>();
    Map<Integer, Set<Integer>> backEdges = new HashMap<>();
    Map<Integer, Integer> sourceLineNumbers = new HashMap<>();

    public void addNode( int iNode ) {
        nodes.add( iNode );
        nodeEdges.computeIfAbsent( iNode, k -> new HashSet<>() );
        backEdges.computeIfAbsent( iNode, k -> new HashSet<>() );
    }

    public void removeNode( int iNode ) {
        if( nodes.contains( iNode ) ) {
            Set<Integer> predecessors = getPredecessors( iNode );
            Set<Integer> successors = getSuccessors( iNode );

            for( int iSuccessor : successors ) {
                // Remove the back edge to the given node from its successor.
                backEdges.get( iSuccessor ).remove( iNode );
            }

            for( int iPredecessor : predecessors ) {
                // Remove the forward edge to the given node from its predecessor.
                nodeEdges.get( iPredecessor ).remove( iNode );

                // Add an edge from the predecessor to each of the given node's successors.
                for( int iSuccessor : successors ) {
                    addEdge( iPredecessor, iSuccessor );
                }
            }

            // Remove the forward edges from the given node to all of its successors.
            // Remove the back edges from the given node to all of its predecessors.
            // Remove the given node from the set of nodes.
            nodeEdges.remove( iNode );
            backEdges.remove( iNode );
            nodes.remove( iNode );
        }
    }

    public void addEdge( int iFrom, int iTo ) {
        addNode( iFrom );
        addNode( iTo );
        nodeEdges.get( iFrom ).add( iTo );
        backEdges.get( iTo ).add( iFrom );
    }

    public void setSourceLineNumber( int iNode, int sourceLineNumber ) {
        nodes.add( iNode );
        sourceLineNumbers.put( iNode, sourceLineNumber );
    }

    public int getSourceLineNumber( int iNode ) {
        return sourceLineNumbers.getOrDefault( iNode, INVALID_LINE );
    }

    public Set<Integer> getNodes() {
        return nodes;
    }

    public Map<Integer, Set<Integer>> getNodeEdges() {
        return nodeEdges;
    }

    public Set<Integer> getPredecessors( int iNode ) {
        return backEdges.get( iNode );
    }

    public Set<Integer> getSuccessors( int iNode ) {
        return nodeEdges.get( iNode );
    }

    public void printDot( PrintStream stream ) {
        stream.println( "digraph {" );
        for( int iFrom : nodeEdges.keySet() ) {
            for( int iTo : nodeEdges.get( iFrom ) ) {
                stream.println( String.format( "    %d -> %d", iFrom, iTo ) );
            }
        }
        stream.println( "}" );
    }
}
