package main.errors;

import main.analysis.FunctionSymbol;
import main.lexer.Token;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.FunctionCall;

import java.util.List;

import static main.util.StringUtils.typeListAsString;

public record ArgumentTypeMismatchError(
        String reason,
        Token startToken,
        Token endToken
) implements SrcCodeError {
    private static final String REASON_TEMPLATE = """
                Argument type mismatch for function '%s'
                Expected: %s
                Actual  : %s(%s)
                """;

    public ArgumentTypeMismatchError(FunctionCall functionCall, List<Type> argumentTypes, FunctionSymbol target) {
        this(
            REASON_TEMPLATE.formatted(
                    functionCall.name(),
                    target.signatureString(),
                    functionCall.name(),
                    typeListAsString(argumentTypes)
            ),
            functionCall.sourceInfo().firstToken(),
            functionCall.sourceInfo().lastToken()
        );
    }

}
