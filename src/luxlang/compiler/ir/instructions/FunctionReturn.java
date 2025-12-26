package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;

public record FunctionReturn(IRValue returnValue) implements TerminatorInstruction {
    @Override
    public String serialize() {
        if (returnValue == null) {
            return "ret void";
        } else {
            return "ret " + returnValue.serialize();
        }
    }
}
