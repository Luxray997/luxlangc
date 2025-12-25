package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record And(
    Temporary destination,
    IRValue operand1,
    IRValue operand2
) implements RegularInstruction {
    @Override
    public String serialize() {
        return destination.serialize() + " = and " + operand1.serialize() + ", " + operand2.serialize();
    }
}
