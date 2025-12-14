package main.parser.nodes.expressions;

import main.parser.objects.SourceInfo;

public record FloatingPointLiteral(
    String value,
    SourceInfo sourceInfo
) implements Expression { }