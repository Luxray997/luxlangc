package main.parser.nodes.expressions;

public sealed interface Expression permits FunctionCall,
                                           BinaryOperation,
                                           UnaryOperation,
                                           VariableExpression,
                                           FloatingPointLiteral,
                                           IntegerLiteral,
                                           BooleanLiteral
{ }