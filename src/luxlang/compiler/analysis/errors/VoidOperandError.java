package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.objects.SourceInfo;

public record VoidOperandError(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisError {
    private static final String REASON = "Operand has void type";

    public VoidOperandError(SourceInfo sourceInfo) {
        this(
            REASON,
            sourceInfo.firstToken(),
            sourceInfo.lastToken()
        );
    }
}