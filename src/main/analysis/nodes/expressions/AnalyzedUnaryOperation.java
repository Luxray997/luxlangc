package main.analysis.nodes.expressions;

import main.parser.nodes.Type;
import main.parser.nodes.expressions.UnaryOperation;

public record AnalyzedUnaryOperation(
    UnaryOperation.UnaryOperationType operation,
    AnalyzedExpression operand,
    Type resultType
) implements AnalyzedExpression { }