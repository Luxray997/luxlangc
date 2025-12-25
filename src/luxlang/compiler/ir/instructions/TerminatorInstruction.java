package luxlang.compiler.ir.instructions;

public sealed interface TerminatorInstruction permits ConditionalBranch, UnconditionalBranch, FunctionReturn {
    /**
     * Serializes the terminator instruction to a deterministic string representation.
     */
    String serialize();
}
