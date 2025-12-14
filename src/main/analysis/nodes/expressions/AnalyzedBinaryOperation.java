package main.analysis.nodes.expressions;

import main.parser.objects.SourceInfo;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.BinaryOperation;

public record AnalyzedBinaryOperation(
    BinaryOperation.BinaryOperationType operation,
    AnalyzedExpression left,
    AnalyzedExpression right,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }