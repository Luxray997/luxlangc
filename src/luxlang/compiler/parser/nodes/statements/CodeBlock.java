package luxlang.compiler.parser.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;

import java.util.List;

public record CodeBlock(
    List<Statement> statements,
    SourceInfo sourceInfo
) implements Statement { }