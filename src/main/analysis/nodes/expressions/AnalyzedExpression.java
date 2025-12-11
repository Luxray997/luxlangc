package main.analysis.nodes.expressions;

import main.parser.SourceInfo;
import main.parser.nodes.Type;

public sealed interface AnalyzedExpression permits AnalyzedFunctionCall,
                                                   AnalyzedBinaryOperation,
                                                   AnalyzedUnaryOperation,
                                                   AnalyzedVariableExpression,
                                                   AnalyzedFloatingPointLiteral,
                                                   AnalyzedIntegerLiteral,
                                                   AnalyzedBooleanLiteral
{
    Type resultType();
    SourceInfo sourceInfo();
}