package main.ir.values;

import main.analysis.nodes.expressions.AnalyzedFloatingPointLiteral;
import main.parser.nodes.Type;

public record FloatingPointConstant(Type type, double value) implements IRValue {
    public static FloatingPointConstant from(AnalyzedFloatingPointLiteral floatingPointLiteral) {
        return new FloatingPointConstant(floatingPointLiteral.type(), floatingPointLiteral.value());
    }
}
