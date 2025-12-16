package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;

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