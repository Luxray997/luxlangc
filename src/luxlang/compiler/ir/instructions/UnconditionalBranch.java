package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.BasicBlock;

public record UnconditionalBranch(BasicBlock target) implements TerminatorInstruction {
}
