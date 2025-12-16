package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

public sealed interface Expression permits FunctionCall,
                                           BinaryOperation,
                                           UnaryOperation,
                                           VariableExpression,
                                           FloatingPointLiteral,
                                           IntegerLiteral,
                                           BooleanLiteral
{
    SourceInfo sourceInfo();
}