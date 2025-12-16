package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.Expression;

import java.util.Optional;

public record VariableDeclaration(
    Type type,
    String name,
    Optional<Expression> initialValue,
    SourceInfo sourceInfo
) implements Statement, ForStatement.Initializer { }