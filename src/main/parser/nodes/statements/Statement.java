package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;

public sealed interface Statement permits CodeBlock,
                                          IfStatement,
                                          WhileStatement,
                                          DoWhileStatement,
                                          ForStatement,
                                          ReturnStatement,
                                          VariableDeclaration,
                                          Assignment
{
    SourceInfo sourceInfo();
}