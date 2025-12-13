package main.parser.nodes;

import main.parser.SourceInfo;

public record Parameter(
    Type type,
    String name,
    SourceInfo sourceInfo
) { }