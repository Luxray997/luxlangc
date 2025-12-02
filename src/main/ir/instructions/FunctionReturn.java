package main.ir.instructions;

import main.ir.values.IRValue;

public record FunctionReturn(IRValue returnValue) implements TerminatorInstruction {
}
