package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;

public record AnalyzedWhileStatement(
    AnalyzedExpression condition,
    AnalyzedStatement body,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }