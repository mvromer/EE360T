package ee360t.controlflow.utility;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ControlFlowAnalyzer extends Analyzer<BasicValue> {
    IndexGraph controlFlow = new IndexGraph();

    public ControlFlowAnalyzer() {
        super( new BasicInterpreter() );
    }

    public IndexGraph getControlFlow() {
        return controlFlow;
    }

    @Override
    protected void newControlFlowEdge( int iFrom, int iTo ) {
        controlFlow.addEdge( iFrom, iTo );
    }
}
