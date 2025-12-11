package main.parser.nodes.expressions;

import main.parser.SourceInfo;
import main.parser.nodes.Type;

public record UnaryOperation(
    UnaryOperationType operation,
    Expression operand,
    SourceInfo sourceInfo
) implements Expression {
    public enum UnaryOperationType {
        LOGICAL_NOT,
        BITWISE_NOT,
        NEGATION
    }

    public static boolean isValid(Type operandType, UnaryOperationType operation) {
        return switch (operation) {
            case NEGATION -> operandType.isSignedNumberType();
            case BITWISE_NOT -> operandType.isIntegerType();
            case LOGICAL_NOT -> operandType == Type.BOOL;
        };
    }
}