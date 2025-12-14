package main.analysis;

import main.analysis.nodes.AnalyzedProgram;
import main.analysis.errors.AnalysisError;

import java.util.List;

public sealed interface AnalysisResult permits AnalysisResult.Failure, AnalysisResult.Success {
    record Success(AnalyzedProgram analyzedProgram) implements AnalysisResult { }
    record Failure(List<AnalysisError> errors) implements AnalysisResult { }

    static AnalysisResult success(AnalyzedProgram analyzedProgram) {
        return new AnalysisResult.Success(analyzedProgram);
    }

    static AnalysisResult failure(List<AnalysisError> errors) {
        return new AnalysisResult.Failure(errors);
    }
}
