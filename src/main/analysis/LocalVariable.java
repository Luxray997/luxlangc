package main.analysis;

import main.parser.nodes.Type;

public record LocalVariable(
    int id,
    String name,
    Type type
) {
}
