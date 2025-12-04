package main.ir.values;

import main.analysis.nodes.expressions.AnalyzedIntegerLiteral;
import main.parser.nodes.Type;

public record IntegerConstant(Type type, long value) implements IRValue {
    public static IntegerConstant from(AnalyzedIntegerLiteral integerLiteral) {
        return new IntegerConstant(integerLiteral.resultType(), integerLiteral.value());
    }
}
