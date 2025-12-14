package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.objects.SourceInfo;

import java.util.Optional;

public record AnalyzedIfStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    Optional<AnalyzedStatement> elseBody,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }