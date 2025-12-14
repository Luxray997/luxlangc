package main.analysis.nodes.statements;

import main.parser.objects.SourceInfo;

public sealed interface AnalyzedStatement permits AnalyzedCodeBlock,
        AnalyzedIfStatement,
        AnalyzedWhileStatement,
        AnalyzedDoWhileStatement,
        AnalyzedForStatement,
        AnalyzedReturnStatement,
        AnalyzedVariableDeclaration,
        AnalyzedAssignment
{
    boolean hasGuaranteedReturn();
    SourceInfo sourceInfo();
}