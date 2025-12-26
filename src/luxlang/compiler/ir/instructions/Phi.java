package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.BasicBlock;
import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Phi(
    Temporary destination,
    BasicBlock block1,
    IRValue value1,
    BasicBlock block2,
    IRValue value2
) implements RegularInstruction {
    @Override
    public String serialize() {
        return destination.serialize() + " = phi [" + value1.serialize() + ", " + block1.label() + "], " +
            "[" + value2.serialize() + ", " + block2.label() + "]";
    }
}
