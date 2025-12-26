package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;

import java.util.Optional;

public record AnalyzedForStatement(
    Optional<Initializer> initializer,
    Optional<AnalyzedExpression> condition,
    Optional<AnalyzedAssignment> update,
    AnalyzedStatement body,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement {
    public sealed interface Initializer permits AnalyzedVariableDeclaration, AnalyzedAssignment { }
}