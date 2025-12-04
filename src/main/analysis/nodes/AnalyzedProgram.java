package main.analysis.nodes;

import java.util.List;

public record AnalyzedProgram(
     List<AnalyzedFunctionDeclaration> functionDeclarations
) { }