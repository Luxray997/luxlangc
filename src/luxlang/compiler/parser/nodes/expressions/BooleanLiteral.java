package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

public record BooleanLiteral(
    Value value,
    SourceInfo sourceInfo
) implements Expression {
    public enum Value {
        TRUE,
        FALSE
    }
}