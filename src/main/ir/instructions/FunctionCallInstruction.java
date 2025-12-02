package main.ir.instructions;

import main.ir.values.IRValue;
import main.ir.values.Temporary;

import java.util.List;

public record FunctionCallInstruction(
    String name,
    Temporary destination,
    List<IRValue> arguments
) implements RegularInstruction { }
