package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.objects.BasicBlock;
import luxlang.compiler.ir.values.IRValue;

public record ConditionalBranch(
    IRValue condition,
    BasicBlock taken,
    BasicBlock notTaken
) implements TerminatorInstruction {
    @Override
    public String serialize() {
        return "br " + condition.serialize() + ", " + taken.label() + ", " + notTaken.label();
    }
}
