package main.parser.nodes.expressions;

public record KnownLiteral(Value value) implements Expression {
    public enum Value {
        TRUE,
        FALSE,
        NULL
    }
}