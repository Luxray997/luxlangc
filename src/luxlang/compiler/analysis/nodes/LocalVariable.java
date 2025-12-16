package luxlang.compiler.analysis.nodes;

import luxlang.compiler.parser.nodes.Type;

public record LocalVariable(
    int id,
    String name,
    Type type
) {
}
