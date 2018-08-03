package ee360t.controlFlowGenerator.utility;

import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ControlFlowAnalyzer extends Analyzer<BasicValue> {
    IndexGraph instructionGraph = new IndexGraph();

    public ControlFlowAnalyzer() {
        super( new BasicInterpreter() );
    }

    public IndexGraph getInstructionGraph() {
        return instructionGraph;
    }

    @Override
    protected void newControlFlowEdge( int iFrom, int iTo ) {
        instructionGraph.addEdge( iFrom, iTo );
    }
}
