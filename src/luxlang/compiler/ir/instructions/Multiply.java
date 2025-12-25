package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Multiply(
    Temporary destination,
    IRValue operand1,
    IRValue operand2
) implements RegularInstruction {
    @Override
    public String serialize() {
        return destination.serialize() + " = mul " + operand1.serialize() + ", " + operand2.serialize();
    }
}
