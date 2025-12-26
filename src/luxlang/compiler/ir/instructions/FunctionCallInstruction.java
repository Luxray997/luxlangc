package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionCallInstruction(
    String name,
    Temporary destination,
    List<IRValue> arguments
) implements RegularInstruction {
    @Override
    public String serialize() {
        String args = arguments.stream()
                .map(IRValue::serialize)
                .collect(Collectors.joining(", "));
        if (destination != null) {
            return destination.serialize() + " = call @" + name + "(" + args + ")";
        } else {
            return "call @" + name + "(" + args + ")";
        }
    }
}
