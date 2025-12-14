package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;

import java.util.List;

public record CodeBlock(
    List<Statement> statements,
    SourceInfo sourceInfo
) implements Statement { }