package luxlang.compiler.ir.instructions;

import luxlang.compiler.ir.BasicBlock;
import luxlang.compiler.ir.values.IRValue;

public record ConditionalBranch(
    IRValue condition,
    BasicBlock taken,
    BasicBlock notTaken
) implements TerminatorInstruction { }
