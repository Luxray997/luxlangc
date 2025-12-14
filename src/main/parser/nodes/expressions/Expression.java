package main.parser.nodes.expressions;

import main.parser.objects.SourceInfo;

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