package main.parser.nodes.expressions;

public record BooleanLiteral(Value value) implements Expression {
    public enum Value {
        TRUE,
        FALSE
    }
}