package main.ir.instructions;

import main.ir.values.IRValue;
import main.ir.values.Temporary;

public record Compare(
    ComparisonType comparisonType,
    Temporary destination,
    IRValue left,
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
