package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

import java.util.Optional;

public record ReturnStatement(
    Optional<Expression> value,
    SourceInfo sourceInfo
) implements Statement { }