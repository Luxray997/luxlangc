package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.objects.SourceInfo;

public record AnalyzedWhileStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }