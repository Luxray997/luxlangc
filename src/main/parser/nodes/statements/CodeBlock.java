package main.parser.nodes.statements;

import java.util.List;

public record CodeBlock(List<Statement> statements) implements Statement { }