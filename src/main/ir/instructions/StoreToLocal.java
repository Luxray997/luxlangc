package main.ir.instructions;

import main.ir.values.IRValue;

public record StoreToLocal(int localId, IRValue value) implements RegularInstruction {
}
