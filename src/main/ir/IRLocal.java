package main.ir;

import main.parser.nodes.Type;

public record IRLocal(
    String name,
    Type type,
    int index
) {
}
