package main.errors;

import main.lexer.Token;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.BinaryOperation;
import main.parser.nodes.expressions.FloatingPointLiteral;
import main.parser.nodes.expressions.IntegerLiteral;
import main.parser.nodes.expressions.UnaryOperation;

public record LiteralOverflowError(
    String reason,
    Token startToken,
    Token endToken
) implements SrcCodeError {
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
