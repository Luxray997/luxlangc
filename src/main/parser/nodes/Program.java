package main.parser.nodes;

import java.util.List;

public record Program(List<FunctionDeclaration> functionDeclarations) { }