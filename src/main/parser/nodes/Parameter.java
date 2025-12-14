package main.parser.nodes;

import main.parser.objects.SourceInfo;

public record Parameter(
    Type type,
    String name,
    SourceInfo sourceInfo
) { }