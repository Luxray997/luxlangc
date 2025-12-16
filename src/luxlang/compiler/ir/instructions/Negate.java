package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Negate(
    Temporary destination,
    IRValue operand
) implements RegularInstruction { }
