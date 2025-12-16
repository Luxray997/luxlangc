package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;

public record UnaryOperation(
    UnaryOperationType operation,
    Expression operand,
    SourceInfo sourceInfo
) implements Expression {
    public enum UnaryOperationType {
        LOGICAL_NOT("!="),
        BITWISE_NOT("~"),
        NEGATION("-");

        private final String lexeme;

        UnaryOperationType(String lexeme) {
            this.lexeme = lexeme;
        }

        public String lexeme() {
            return lexeme;
        }
    }

    public static boolean isValid(Type operandType, UnaryOperationType operation) {
        return switch (operation) {
            case NEGATION -> operandType.isSignedNumberType();
            case BITWISE_NOT -> operandType.isIntegerType();
            case LOGICAL_NOT -> operandType == Type.BOOL;
        };
    }
}