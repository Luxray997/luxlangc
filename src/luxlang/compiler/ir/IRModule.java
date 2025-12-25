package luxlang.compiler.ir;

import java.util.List;
import java.util.stream.Collectors;

public record IRModule(List<IRFunction> functions) {
    public String serialize() {
        return functions.stream()
                .map(IRFunction::serialize)
                .collect(Collectors.joining("\n\n"));
    }
}
