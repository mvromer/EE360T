package ee360t.controlflow.utility;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.ListIterator;

// Custom analyzer that will record the control flow edges found by the ASM method analyzer.
public class ControlFlowAnalyzer extends Analyzer<BasicValue> {
    IndexGraph controlFlow = new IndexGraph();

    public static IndexGraph buildControlFlow( String ownerName, MethodNode method ) {
        ControlFlowAnalyzer analyzer = new ControlFlowAnalyzer();

        try {
            // ASM injects label, line number, and frame nodes in the instruction list, even though they aren't real
            // instructions. We don't want those fake instructions in the control flow graph we use for this project,
            // but of course simply removing them prior to running the analyzer that builds the CFG would've been too
            // simple.
            //
            // Instead, we build up the index graph and then do a pass on it after running the analyzer to prune out
            // those nodes that correspond to the fake instructions we don't want. We don't bother rebasing indices,
            // which has the advantage of being able to look up the real instruction nodes easily from our pruned index
            // graph at a later time.
            //

            analyzer.analyze( ownerName, method );

            // Add an edge from the well-defined entry node to the first instruction in the control flow graph, which is
            // always at index 0. Do this now so that the entry node points to the first real instruction after pruning.
            analyzer.controlFlow.addEdge( IndexGraph.ENTRY, 0 );

            // Go through the list of instructions and remove nodes from our control flow graph corresponding to fake
            // instructions signifying labels, line numbers, and stack frames.
            int iInstruction = 0;
            ListIterator<AbstractInsnNode> instructionIter = method.instructions.iterator();
            while( instructionIter.hasNext() ) {
                AbstractInsnNode currentInstruction = instructionIter.next();
                int instructionType = currentInstruction.getType();
                int opcode = currentInstruction.getOpcode();

                if( instructionType == AbstractInsnNode.LABEL ||
                        instructionType == AbstractInsnNode.LINE ||
                        instructionType == AbstractInsnNode.FRAME ) {
                    analyzer.controlFlow.removeNode( iInstruction );
                }
                else if( opcode == Opcodes.ARETURN ||
                        opcode == Opcodes.DRETURN ||
                        opcode == Opcodes.FRETURN ||
                        opcode == Opcodes.IRETURN ||
                        opcode == Opcodes.LRETURN ||
                        opcode == Opcodes.RETURN ) {
                    // Add an edge from this return statement to the well-defined exit node for this method.
                    analyzer.controlFlow.addEdge( iInstruction, IndexGraph.EXIT );
                }

                ++iInstruction;
            }
        }
        catch( AnalyzerException ex ) {
            throw new RuntimeException( ex );
        }

        return analyzer.controlFlow;
    }

    public ControlFlowAnalyzer() {
        super( new BasicInterpreter() );
    }

    @Override
    protected void newControlFlowEdge( int iFrom, int iTo ) {
        controlFlow.addEdge( iFrom, iTo );
    }
}
