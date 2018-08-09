package ee360t.controlflow.trace.agent;

import java.util.ArrayList;
import java.util.List;

public class TraceRecord {
    private String label;
    private List<Integer> tracePath = new ArrayList<>();

    public TraceRecord( String testClassName, String testMethodName, String testMethodDescriptor ) {
        label = testClassName + "." + testMethodName + testMethodDescriptor;
    }

    public void addNode( int globalId ) {
        tracePath.add( globalId );
    }

    String getLabel() {
        return label;
    }

    List<Integer> getTracePath() {
        return tracePath;
    }
}
