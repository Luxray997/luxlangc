package main.ir;

import main.analysis.nodes.LocalVariable;
import main.parser.nodes.Type;

public record IRLocal(
    String name,
    Type type,
    int index
) {
    public static IRLocal from(LocalVariable localVariable) {
        return new IRLocal(localVariable.name(), localVariable.type(), localVariable.id());
    }
}
