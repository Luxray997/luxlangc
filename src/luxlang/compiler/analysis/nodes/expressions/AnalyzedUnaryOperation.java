package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.UnaryOperation;

public record AnalyzedUnaryOperation(
    UnaryOperation.UnaryOperationType operation,
    AnalyzedExpression operand,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }