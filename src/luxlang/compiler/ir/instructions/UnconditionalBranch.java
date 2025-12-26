package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.objects.BasicBlock;

public record UnconditionalBranch(BasicBlock target) implements TerminatorInstruction {
    @Override
    public String serialize() {
        return "br " + target.label();
    }
}
