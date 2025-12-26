package luxlang.compiler.parser.nodes;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.statements.CodeBlock;

import java.util.List;

public record FunctionDeclaration(
    Type returnType,
    String name,
    List<Parameter> parameters,
    CodeBlock body,
    SourceInfo sourceInfo
) { }