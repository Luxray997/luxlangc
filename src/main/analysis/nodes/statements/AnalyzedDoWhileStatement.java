package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.SourceInfo;

public record AnalyzedDoWhileStatement(
    AnalyzedStatement body,
    AnalyzedExpression condition,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }