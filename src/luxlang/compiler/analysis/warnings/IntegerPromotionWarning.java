package luxlang.compiler.analysis.warnings;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.objects.SourceInfo;

public record IntegerPromotionWarning(
    String reason,
    Token startToken,
    Token endToken
) implements AnalysisWarning {
    private static final String REASON_TEMPLATE = "Implicit promotion from '%s' to '%s'";

    public IntegerPromotionWarning(SourceInfo sourceInfo, Type from, Type to) {
        this(
            REASON_TEMPLATE.formatted(from.lexeme(), to.lexeme()),
            sourceInfo.firstToken(),
            sourceInfo.lastToken()
        );
    }
}