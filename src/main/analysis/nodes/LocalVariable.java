package main.analysis.nodes;

import main.parser.nodes.Type;

public record LocalVariable(
    int id,
    String name,
    Type type
) {
}
