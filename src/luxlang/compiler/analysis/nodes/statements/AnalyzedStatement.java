package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;

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