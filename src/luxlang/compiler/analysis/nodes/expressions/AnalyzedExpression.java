package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;

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