package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;

import java.util.Optional;

public record AnalyzedIfStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    Optional<AnalyzedStatement> elseBody,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }