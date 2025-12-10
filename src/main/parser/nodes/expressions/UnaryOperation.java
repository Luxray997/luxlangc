package main.parser.nodes.expressions;

public record UnaryOperation(
    UnaryOperationType operation,
    Expression operand
) implements Expression {
    public enum UnaryOperationType {
        LOGICAL_NOT,
        BITWISE_NOT,
        NEGATION
    }
}