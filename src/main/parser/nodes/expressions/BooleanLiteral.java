package main.parser.nodes.expressions;

import main.parser.objects.SourceInfo;

public record BooleanLiteral(
    Value value,
    SourceInfo sourceInfo
) implements Expression {
    public enum Value {
        TRUE,
        FALSE
    }
}