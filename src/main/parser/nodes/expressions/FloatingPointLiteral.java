package main.parser.nodes.expressions;

import main.parser.SourceInfo;

public record FloatingPointLiteral(
    String value,
    SourceInfo sourceInfo
) implements Expression { }