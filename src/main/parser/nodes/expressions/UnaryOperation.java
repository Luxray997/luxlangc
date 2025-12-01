package main.parser.nodes.expressions;

public record UnaryOperation(
    UnaryOperation.Type operation,
    Expression operand
) implements Expression {
    public enum Type {
        LOGICAL_NOT,
        BITWISE_NOT,
        NEGATION
    }
}