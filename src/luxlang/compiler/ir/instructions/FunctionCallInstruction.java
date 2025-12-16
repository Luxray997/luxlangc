package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;
import luxlang.compiler.ir.values.Temporary;

import java.util.List;

public record FunctionCallInstruction(
    String name,
    Temporary destination,
    List<IRValue> arguments
) implements RegularInstruction { }
