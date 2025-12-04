package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;

public record AnalyzedWhileStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    boolean hasGuaranteedReturn
) implements AnalyzedStatement { }