package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

public record WhileStatement(
    Expression condition,
    Statement body,
    SourceInfo sourceInfo
) implements Statement { }