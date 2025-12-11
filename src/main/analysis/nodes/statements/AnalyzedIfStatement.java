package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.SourceInfo;
import main.parser.nodes.statements.IfStatement;

import java.util.Optional;

public record AnalyzedIfStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    Optional<AnalyzedStatement> elseBody,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }