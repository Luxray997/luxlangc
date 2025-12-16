package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

import java.util.Optional;

public record IfStatement(
    Expression condition,
    Statement body,
    Optional<Statement> elseBody,
    SourceInfo sourceInfo
) implements Statement { }