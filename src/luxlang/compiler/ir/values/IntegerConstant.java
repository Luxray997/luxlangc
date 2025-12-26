package luxlang.compiler.ir.values;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedIntegerLiteral;
import luxlang.compiler.parser.nodes.Type;

public record IntegerConstant(Type type, long value) implements IRValue {
    public static IntegerConstant from(AnalyzedIntegerLiteral integerLiteral) {
        return new IntegerConstant(integerLiteral.resultType(), integerLiteral.value());
    }

    @Override
    public String serialize() {
        return String.valueOf(value);
    }
}
