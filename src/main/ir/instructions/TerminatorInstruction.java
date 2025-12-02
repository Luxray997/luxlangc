package main.ir.instructions;

public sealed interface TerminatorInstruction permits ConditionalBranch, UnconditionalBranch, FunctionReturn { }
