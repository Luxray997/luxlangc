package luxlang.compiler.parser.nodes;

import luxlang.compiler.parser.objects.SourceInfo;

public record Parameter(
    Type type,
    String name,
    SourceInfo sourceInfo
) { }