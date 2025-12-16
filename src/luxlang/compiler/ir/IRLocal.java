package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.parser.nodes.Type;

public record IRLocal(
    String name,
    Type type,
    int index
) {
    public static IRLocal from(LocalVariable localVariable) {
        return new IRLocal(localVariable.name(), localVariable.type(), localVariable.id());
    }
}
