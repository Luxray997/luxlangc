package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.FloatingPointLiteral;
import luxlang.compiler.parser.nodes.expressions.IntegerLiteral;

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
