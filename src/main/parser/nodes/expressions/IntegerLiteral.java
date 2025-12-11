package main.parser.nodes.expressions;

import main.parser.SourceInfo;

public record IntegerLiteral(
    String value,
    SourceInfo sourceInfo
) implements Expression { }