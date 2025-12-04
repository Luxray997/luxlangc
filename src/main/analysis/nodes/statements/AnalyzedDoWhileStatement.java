package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;

public record AnalyzedDoWhileStatement(
    AnalyzedStatement body,
    AnalyzedExpression condition,
    boolean hasGuaranteedReturn
) implements AnalyzedStatement { }