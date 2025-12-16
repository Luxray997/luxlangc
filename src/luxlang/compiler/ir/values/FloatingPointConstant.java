package luxlang.compiler.ir.values;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedFloatingPointLiteral;
import luxlang.compiler.parser.nodes.Type;

public record FloatingPointConstant(Type type, double value) implements IRValue {
    public static FloatingPointConstant from(AnalyzedFloatingPointLiteral floatingPointLiteral) {
        return new FloatingPointConstant(floatingPointLiteral.type(), floatingPointLiteral.value());
    }
}
