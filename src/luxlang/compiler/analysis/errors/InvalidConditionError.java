package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.statements.DoWhileStatement;
import luxlang.compiler.parser.nodes.statements.ForStatement;
import luxlang.compiler.parser.nodes.statements.IfStatement;
import luxlang.compiler.parser.nodes.statements.WhileStatement;

public record InvalidConditionError(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE_FOR = "For loop contains non-boolean condition";
    private static final String REASON_TEMPLATE_WHILE = "While loop contains non-boolean condition";
    private static final String REASON_TEMPLATE_DO_WHILE = "Do-while loop contains non-boolean condition";
    private static final String REASON_TEMPLATE_IF = "If statement contains non-boolean condition";

    public InvalidConditionError(ForStatement forStatement) {
        this(
            REASON_TEMPLATE_FOR,
            forStatement.sourceInfo().firstToken(),
            forStatement.sourceInfo().lastToken()
        );
    }

    public InvalidConditionError(WhileStatement whileStatement) {
        this(
            REASON_TEMPLATE_WHILE,
            whileStatement.sourceInfo().firstToken(),
            whileStatement.sourceInfo().lastToken()
        );
    }

    public InvalidConditionError(DoWhileStatement doWhileStatement) {
        this(
            REASON_TEMPLATE_DO_WHILE,
            doWhileStatement.sourceInfo().firstToken(),
            doWhileStatement.sourceInfo().lastToken()
        );
    }

    public InvalidConditionError(IfStatement ifStatement) {
        this(
            REASON_TEMPLATE_IF,
            ifStatement.sourceInfo().firstToken(),
            ifStatement.sourceInfo().lastToken()
        );
    }

}
