package ee360t.controlflow.trace.agent;

import java.util.*;

public class TraceRecord {
    private String testClassName;
    private String testMethodName;
    private String testMethodDescriptor;

    private List<Integer> tracePath = new ArrayList<>();

    private Map<Integer, Set<Integer>> callEdges = new HashMap<>();
    private Stack<Integer> callStack = new Stack<>();

    public TraceRecord( String testClassName, String testMethodName, String testMethodDescriptor ) {
        this.testClassName = testClassName;
        this.testMethodName = testMethodName;
        this.testMethodDescriptor = testMethodDescriptor;
    }

    public void addNode( int globalId ) {
        tracePath.add( globalId );
    }

    public void pushMethod( int globalMethodId ) {
        // If the call stack isn't empty, then record the call edge from the current method at the top of the stack to
        // the method we were just given.
        if( !callStack.empty() ) {
            callEdges.computeIfAbsent( callStack.peek(), key -> new HashSet<>() ).add( globalMethodId );
        }

        callStack.push( globalMethodId );
    }

    public void popMethod() {
        callStack.pop();
    }

    public String getTestClassName() {
        return testClassName;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public String getTestMethodDescriptor() {
        return testMethodDescriptor;
    }

    String getLabel() {
        return testClassName + "." + testMethodName + testMethodDescriptor;
    }

    List<Integer> getTracePath() {
        return tracePath;
    }

    public Map<Integer, Set<Integer>> getCallEdges() {
        return callEdges;
    }
}
