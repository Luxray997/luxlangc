package main.parser.nodes.expressions;

import main.parser.SourceInfo;

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