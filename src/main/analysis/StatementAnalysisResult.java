package main.analysis;

import main.analysis.nodes.statements.AnalyzedStatement;

public record StatementAnalysisResult<T extends AnalyzedStatement>(
    boolean hasGuaranteedReturn,
    T analyzedNode
) { }