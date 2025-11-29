package main.parser.nodes.expressions;

public record BinaryOperation(
    BinaryOperation.Type type,
    Expression left,
    Expression right
) implements Expression {
    public enum Type {
        ADD,
        SUB,
        MULT,
        DIV,
        MOD,
        LOGICAL_AND,
        LOGICAL_OR,
        BITWISE_AND,
        BITWISE_OR,
        BITWISE_XOR,
        EQUAL,
        NOT_EQUAL,
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL,
    }
}