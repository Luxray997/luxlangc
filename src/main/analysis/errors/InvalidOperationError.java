package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.BinaryOperation;
import main.parser.nodes.expressions.UnaryOperation;

public record InvalidOperationError(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE_UNARY = "Unary operation '%s' is not allowed on operand of type '%s'";
    private static final String REASON_TEMPLATE_BINARY = "Binary operation '%s' is not allowed between operands of type '%s' and '%s'";

    public InvalidOperationError(UnaryOperation unaryOperation, Type operandType) {
        this(
            REASON_TEMPLATE_UNARY.formatted(unaryOperation.operation().lexeme(), operandType.lexeme()),
            unaryOperation.sourceInfo().firstToken(),
            unaryOperation.sourceInfo().lastToken()
        );
    }

    public InvalidOperationError(BinaryOperation binaryOperation, Type leftType, Type rightType) {
        this(
            REASON_TEMPLATE_BINARY.formatted(binaryOperation.operation().lexeme(), leftType.lexeme(), rightType.lexeme()),
            binaryOperation.sourceInfo().firstToken(),
            binaryOperation.sourceInfo().lastToken()
        );
    }
}
