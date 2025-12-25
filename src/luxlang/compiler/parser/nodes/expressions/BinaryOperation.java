package luxlang.compiler.parser.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;

public record BinaryOperation(
    BinaryOperationType operation,
    Expression left,
    Expression right,
    SourceInfo sourceInfo
) implements Expression {
    public enum BinaryOperationType {
        ADD("+"),
        SUB("-"),
        MULT("*"),
        DIV("/"),
        MOD("%"),
        LOGICAL_AND("&&"),
        LOGICAL_OR("||"),
        BITWISE_AND("&"),
        BITWISE_OR("|"),
        BITWISE_XOR("^"),
        EQUAL("=="),
        NOT_EQUAL("!="),
        LESS("<"),
        LESS_EQUAL("<="),
        GREATER(">"),
        GREATER_EQUAL(">=");

        private final String lexeme;

        BinaryOperationType(String lexeme) {
            this.lexeme = lexeme;
        }

        public String lexeme() {
            return lexeme;
        }

        public boolean isComparisonOperation() {
            return switch (this) {
                case EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL -> true;
                default -> false;
            };
        }

        public boolean isLogicalOperation() {
            return switch (this) {
                case LOGICAL_AND, LOGICAL_OR -> true;
                default -> false;
            };
        }

        public boolean isEqualityOperation() {
            return switch (this) {
                case EQUAL, NOT_EQUAL -> true;
                default -> false;
            };
        }
    }
}