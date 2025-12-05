package main.ir.instructions;

import main.ir.values.IRValue;
import main.ir.values.Temporary;

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
