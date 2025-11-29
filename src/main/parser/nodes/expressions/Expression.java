package main.parser.nodes.expressions;

public sealed interface Expression permits BinaryOperation,
                                           FloatingPointLiteral,
                                           FunctionCall,
                                           IntegerLiteral,
                                           KnownLiteral,
                                           UnaryOperation,
                                           VariableExpression
{ }