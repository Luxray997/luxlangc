package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Modulo(
    Temporary destination,
    IRValue operand1,
    IRValue operand2
) implements RegularInstruction {
    @Override
    public String serialize() {
        return destination.serialize() + " = mod " + operand1.serialize() + ", " + operand2.serialize();
    }
}
