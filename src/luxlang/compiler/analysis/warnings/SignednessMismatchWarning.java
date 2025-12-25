package luxlang.compiler.analysis.warnings;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.objects.SourceInfo;

public record SignednessMismatchWarning(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisWarning {
    private static final String REASON_TEMPLATE = "Operation between signed and unsigned integer types may produce unexpected results";

    public SignednessMismatchWarning(SourceInfo sourceInfo) {
        this(
            REASON_TEMPLATE,
            sourceInfo.firstToken(),
            sourceInfo.lastToken()
        );
    }
}