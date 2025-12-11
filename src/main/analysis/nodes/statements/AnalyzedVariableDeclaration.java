package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.SourceInfo;
import main.parser.nodes.Type;
import main.parser.nodes.statements.VariableDeclaration;

import java.util.Optional;

public record AnalyzedVariableDeclaration(
    Type type,
    String name,
    Optional<AnalyzedExpression> initialValue,
    SourceInfo sourceInfo
) implements AnalyzedStatement, AnalyzedForStatement.Initializer {
    @Override
    public boolean hasGuaranteedReturn() {
        return false;
    }

    public static AnalyzedVariableDeclaration from(VariableDeclaration variableDeclaration, Optional<AnalyzedExpression> initialValue) {
        return new AnalyzedVariableDeclaration(variableDeclaration.type(), variableDeclaration.name(), initialValue, variableDeclaration.sourceInfo());
    }
}