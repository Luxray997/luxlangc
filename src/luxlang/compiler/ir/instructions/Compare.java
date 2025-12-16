package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Compare(
        Temporary destination,
        IRValue left,
        ComparisonType comparisonType,
        IRValue right
) implements RegularInstruction {
    public enum ComparisonType {
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL,
        EQUAL,
        NOT_EQUAL,
    }
}
