package main.parser.nodes.statements;

public sealed interface Statement permits CodeBlock,
                                          IfStatement,
                                          WhileStatement,
                                          DoWhileStatement,
                                          ForStatement,
                                          ReturnStatement,
                                          VariableDeclaration,
                                          Assignment
{ }