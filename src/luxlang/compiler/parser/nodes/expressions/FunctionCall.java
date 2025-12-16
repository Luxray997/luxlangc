package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

import java.util.List;

public record FunctionCall(
    String name,
    List<Expression> arguments,
    SourceInfo sourceInfo
) implements Expression { }