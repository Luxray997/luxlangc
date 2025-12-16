package luxlang.compiler.ir.instructions;

public sealed interface TerminatorInstruction permits ConditionalBranch, UnconditionalBranch, FunctionReturn { }
