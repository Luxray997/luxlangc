package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

public record DoWhileStatement(
    Statement body,
    Expression condition,
    SourceInfo sourceInfo
) implements Statement { }