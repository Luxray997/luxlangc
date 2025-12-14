package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.FloatingPointLiteral;
import main.parser.nodes.expressions.IntegerLiteral;

public record LiteralOverflowError(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE = "Value does not fit in '%s' data type";

    public LiteralOverflowError(IntegerLiteral literal, Type dataType) {
        this(
            REASON_TEMPLATE.formatted(dataType.lexeme()),
            literal.sourceInfo().firstToken(),
            literal.sourceInfo().lastToken()
        );
    }

    public LiteralOverflowError(FloatingPointLiteral literal, Type dataType) {
        this(
            REASON_TEMPLATE.formatted(dataType.lexeme()),
            literal.sourceInfo().firstToken(),
            literal.sourceInfo().lastToken()
        );
    }

}
