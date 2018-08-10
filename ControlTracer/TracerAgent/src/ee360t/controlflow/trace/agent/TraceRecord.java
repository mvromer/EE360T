package ee360t.controlflow.trace.agent;

import java.util.ArrayList;
import java.util.List;

public class TraceRecord {
    private String testClassName;
    private String testMethodName;
    private String testMethodDescriptor;
    private List<Integer> tracePath = new ArrayList<>();

    public TraceRecord( String testClassName, String testMethodName, String testMethodDescriptor ) {
        this.testClassName = testClassName;
        this.testMethodName = testMethodName;
        this.testMethodDescriptor = testMethodDescriptor;
    }

    public void addNode( int globalId ) {
        tracePath.add( globalId );
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
}
