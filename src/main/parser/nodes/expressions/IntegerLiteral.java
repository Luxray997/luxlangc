package main.parser.nodes.expressions;

import main.parser.objects.SourceInfo;

public record IntegerLiteral(
    String value,
    SourceInfo sourceInfo
) implements Expression { }