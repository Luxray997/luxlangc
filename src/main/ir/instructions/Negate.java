package main.ir.instructions;

import main.ir.values.IRValue;
import main.ir.values.Temporary;

public record Negate(
    Temporary destination,
    IRValue operand
) implements RegularInstruction { }
