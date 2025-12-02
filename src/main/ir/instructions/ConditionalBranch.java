package main.ir.instructions;

import main.ir.BasicBlock;
import main.ir.values.IRValue;

public record ConditionalBranch(
    IRValue condition,
    BasicBlock taken,
    BasicBlock notTaken
) implements TerminatorInstruction { }
