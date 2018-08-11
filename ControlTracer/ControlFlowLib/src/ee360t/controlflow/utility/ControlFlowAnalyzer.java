package ee360t.controlflow.utility;

import ee360t.controlflow.model.NodeId;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

// Custom analyzer that will record the control flow edges found by the ASM method analyzer.
public class ControlFlowAnalyzer extends Analyzer<BasicValue> {
    ControlFlow controlFlow = new ControlFlow();

    public static ControlFlow buildControlFlow( String ownerName, MethodNode method ) {
        return buildControlFlow( ownerName, method, null );
    }

    public static ControlFlow buildControlFlow( String ownerName, MethodNode method,
                                                Map<NodeId, Set<NodeId>> intraclassEdges ) {
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
            analyzer.controlFlow.addEdge( ControlFlow.ENTRY, 0 );

            // Go through the list of instructions and remove nodes from our control flow graph corresponding to fake
            // instructions signifying labels, line numbers, and stack frames.
            int iInstruction = 0;
            int lineNumber = ControlFlow.INVALID_LINE;
            NodeId calleeExitId = null;
            ListIterator<AbstractInsnNode> instructionIter = method.instructions.iterator();

            while( instructionIter.hasNext() ) {
                AbstractInsnNode currentInstruction = instructionIter.next();
                int instructionType = currentInstruction.getType();

                if( instructionType == AbstractInsnNode.LABEL ||
                        instructionType == AbstractInsnNode.LINE ||
                        instructionType == AbstractInsnNode.FRAME ) {
                    // If this is a line number, record it so that we can associate it with the next real instruction.
                    if( instructionType == AbstractInsnNode.LINE )
                        lineNumber = ((LineNumberNode)currentInstruction).line;

                    analyzer.controlFlow.removeNode( iInstruction );
                }
                else {
                    // Associate this "real" instruction with the most recent line number of its valid. After
                    // associating, we reset the line number so that at most one control flow node maps to the
                    // associated line number instruction node.
                    if( lineNumber != ControlFlow.INVALID_LINE ) {
                        analyzer.controlFlow.setSourceLineNumber( iInstruction, lineNumber );
                        lineNumber = ControlFlow.INVALID_LINE;
                    }

                    // If we have a callee exit node from a previous iteration, that means the last "real" instruction
                    // was an invocation. The current "real" instruction is the return site, so form an intraclass edge
                    // from the callee exit node to this node.
                    if( calleeExitId != null ) {
                        NodeId returnSiteId = new NodeId( ownerName, method.name, method.desc, iInstruction );
                        intraclassEdges.computeIfAbsent( calleeExitId, key -> new HashSet<>() ).add( returnSiteId );

                        // Reset the callee exit node so that we don't form spurious intraclass edges.
                        calleeExitId = null;
                    }

                    int opcode = currentInstruction.getOpcode();
                    if( opcode == Opcodes.ARETURN ||
                            opcode == Opcodes.DRETURN ||
                            opcode == Opcodes.FRETURN ||
                            opcode == Opcodes.IRETURN ||
                            opcode == Opcodes.LRETURN ||
                            opcode == Opcodes.RETURN ) {
                        // Add an edge from this return statement to the well-defined exit node for this method.
                        analyzer.controlFlow.addEdge( iInstruction, ControlFlow.EXIT );
                    }
                    else if( intraclassEdges != null && currentInstruction instanceof MethodInsnNode ) {
                        MethodInsnNode invokeInstruction = (MethodInsnNode)currentInstruction;

                        // TODO: May need to add an additional check here to see if the invoke instruction's owner is a
                        // base class of the given owner. Because then it's conceivable that we virtually call a method
                        // inside of our class.
                        if( invokeInstruction.owner.equals( ownerName ) ) {
                            // This invoke instruction calls another method inside of our class. Record the intraclass
                            // edge that goes from the invoke instruction to the called method. Save the called method's
                            // exit node so that another intraclass edge can be formed between it and the return site in
                            // this method, which is the first real instruction following this invoke instruction.
                            NodeId callSiteId = new NodeId( ownerName, method.name, method.desc, iInstruction );
                            NodeId calleeEntryId = new NodeId( invokeInstruction.owner, invokeInstruction.name,
                                    invokeInstruction.desc, ControlFlow.ENTRY );
                            calleeExitId = new NodeId( invokeInstruction.owner, invokeInstruction.name,
                                    invokeInstruction.desc, ControlFlow.EXIT );

                            intraclassEdges.computeIfAbsent( callSiteId, key -> new HashSet<>() ).add( calleeEntryId );
                        }
                    }
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
