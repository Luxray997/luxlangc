package main.analysis.nodes.statements;

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
}