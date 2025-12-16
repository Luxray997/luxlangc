package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record And(
    Temporary destination,
    IRValue operand1,
    IRValue operand2
) implements RegularInstruction { }
