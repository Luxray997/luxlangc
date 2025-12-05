package main.ir.values;

import main.analysis.nodes.expressions.AnalyzedBooleanLiteral;
import main.parser.nodes.Type;

public record BooleanConstant(boolean value) implements IRValue {
    public static BooleanConstant from(AnalyzedBooleanLiteral booleanLiteral) {
        return new BooleanConstant(booleanLiteral.value());
    }

    @Override
    public Type type() {
        return Type.BOOL;
    }
}