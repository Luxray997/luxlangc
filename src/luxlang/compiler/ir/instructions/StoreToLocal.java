package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.values.IRValue;

public record StoreToLocal(int localId, IRValue value) implements RegularInstruction {
}
