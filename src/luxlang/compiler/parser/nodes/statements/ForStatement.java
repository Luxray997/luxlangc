package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

import java.util.Optional;

public record ForStatement(
    Optional<Initializer> initializer,
    Optional<Expression> condition,
    Optional<Assignment> update,
    Statement body,
    SourceInfo sourceInfo
) implements Statement {
    public sealed interface Initializer permits VariableDeclaration, Assignment { }
}