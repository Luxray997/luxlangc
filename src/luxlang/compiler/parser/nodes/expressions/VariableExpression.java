package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

public record VariableExpression(
    String name,
    SourceInfo sourceInfo
) implements Expression { }