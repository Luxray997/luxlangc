package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.expressions.Expression;

public record Assignment(
    String variableName,
    Expression value,
    SourceInfo sourceInfo
) implements Statement, ForStatement.Initializer { }