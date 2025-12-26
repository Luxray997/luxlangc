package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

public record Compare(
        Temporary destination,
        IRValue left,
        ComparisonType comparisonType,
        IRValue right
) implements RegularInstruction {
    @Override
    public String serialize() {
        return destination.serialize() + " = cmp " + comparisonType.serialize() + " " +
            left.serialize() + ", " + right.serialize();
    }

    public enum ComparisonType {
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL,
        EQUAL,
        NOT_EQUAL;

        public String serialize() {
            return switch (this) {
                case LESS -> "lt";
                case LESS_EQUAL -> "le";
                case GREATER -> "gt";
                case GREATER_EQUAL -> "ge";
                case EQUAL -> "eq";
                case NOT_EQUAL -> "ne";
            };
        }
    }
}
