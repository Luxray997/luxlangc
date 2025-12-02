package main.ir.instructions;

import main.ir.BasicBlock;

public record UnconditionalBranch(BasicBlock target) implements TerminatorInstruction {
}
