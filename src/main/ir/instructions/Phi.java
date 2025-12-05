package main.ir.instructions;

import main.ir.BasicBlock;
import main.ir.values.IRValue;
import main.ir.values.Temporary;

public record Phi(
    Temporary destination,
    BasicBlock block1,
    IRValue value1,
    BasicBlock block2,
    IRValue value2
) implements RegularInstruction { }
