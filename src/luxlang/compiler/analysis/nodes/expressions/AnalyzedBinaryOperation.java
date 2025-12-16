package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation;

public record AnalyzedBinaryOperation(
    BinaryOperation.BinaryOperationType operation,
    AnalyzedExpression left,
    AnalyzedExpression right,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }