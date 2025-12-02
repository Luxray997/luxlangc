package main.ir.instructions;

import main.ir.values.IRValue;
import main.ir.values.Temporary;

public record Xor(
    Temporary destination,
    IRValue operand1,
    IRValue operand2
) implements RegularInstruction { }
