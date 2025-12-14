package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.objects.SourceInfo;

import java.util.Optional;

public record AnalyzedReturnStatement(
    Optional<AnalyzedExpression> value,
    SourceInfo sourceInfo
) implements AnalyzedStatement {
    public boolean hasGuaranteedReturn() {
        return true;
    }
}