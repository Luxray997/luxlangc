package luxlang.compiler.ir.values;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedBooleanLiteral;
import luxlang.compiler.parser.nodes.Type;

public record BooleanConstant(boolean value) implements IRValue {
    public static BooleanConstant from(AnalyzedBooleanLiteral booleanLiteral) {
        return new BooleanConstant(booleanLiteral.value());
    }

    @Override
    public Type type() {
        return Type.BOOL;
    }
}