package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

public record FloatingPointLiteral(
    String value,
    SourceInfo sourceInfo
) implements Expression { }