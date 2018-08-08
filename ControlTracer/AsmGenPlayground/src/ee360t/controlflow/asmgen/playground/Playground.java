package ee360t.controlflow.asmgen.playground;

import ee360t.controlflow.trace.agent.TraceRegistry;

public class Playground {
    static void callStartNewTraceWithString() {
        TraceRegistry.startNewTrace( "Class", "foo", "()V" );
    }

    static void callVisitNode() {
        TraceRegistry.visitNode( -2 );
        TraceRegistry.visitNode( -1 );
        TraceRegistry.visitNode( 0 );
        TraceRegistry.visitNode( 1 );
        TraceRegistry.visitNode( 2 );
        TraceRegistry.visitNode( 3 );
        TraceRegistry.visitNode( 4 );
        TraceRegistry.visitNode( 5 );
        TraceRegistry.visitNode( 6 );
        TraceRegistry.visitNode( 127 );
        TraceRegistry.visitNode( 128 );
        TraceRegistry.visitNode( 32767 );
        TraceRegistry.visitNode( 32768 );
    }
}
