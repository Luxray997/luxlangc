package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;

import java.util.Optional;

public record AnalyzedReturnStatement(
    Optional<AnalyzedExpression> value,
    SourceInfo sourceInfo
) implements AnalyzedStatement {
    @Override
    public boolean hasGuaranteedReturn() {
        return true;
    }
}