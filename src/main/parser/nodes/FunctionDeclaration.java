package main.parser.nodes;

import main.parser.nodes.statements.CodeBlock;

import java.util.List;

public record FunctionDeclaration(
    Type returnType,
    String name,
    List<Parameter> parameters,
    CodeBlock body
) { }