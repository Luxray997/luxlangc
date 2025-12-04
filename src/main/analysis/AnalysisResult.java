package main.analysis;

import main.analysis.nodes.AnalyzedProgram;
import main.errors.SemanticError;

import java.util.List;

public record AnalysisResult(
    AnalyzedProgram program,
    List<SemanticError> errors
) {
}
