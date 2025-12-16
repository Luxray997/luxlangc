package luxlang.compiler.analysis.nodes;

import java.util.List;

public record AnalyzedProgram(
     List<AnalyzedFunctionDeclaration> functionDeclarations
) { }