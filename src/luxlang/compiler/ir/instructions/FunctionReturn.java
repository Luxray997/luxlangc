package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;

public record FunctionReturn(IRValue returnValue) implements TerminatorInstruction {
}
