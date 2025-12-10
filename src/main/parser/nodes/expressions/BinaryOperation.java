package main.parser.nodes.expressions;

import main.parser.nodes.Type;

import java.util.Optional;

public record BinaryOperation(
    BinaryOperationType operation,
    Expression left,
    Expression right
) implements Expression {
    public enum BinaryOperationType {
        ADD,
        SUB,
        MULT,
        DIV,
        MOD,
        LOGICAL_AND,
        LOGICAL_OR,
        BITWISE_AND,
        BITWISE_OR,
        BITWISE_XOR,
        EQUAL,
        NOT_EQUAL,
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL;

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

    private static Type getNumberResultType(Type leftType, Type rightType) {
        if (leftType.isFloatingPointType() || rightType.isFloatingPointType()) {
            Type resultType = leftType == Type.DOUBLE || rightType == Type.DOUBLE ? Type.DOUBLE : Type.FLOAT;

            if (leftType != rightType) {
                // implicit conversion warning
            }

            return resultType;
        }
         return getIntegerResultType(leftType, rightType);
    }

    public static Type getIntegerResultType(Type leftType, Type rightType) {
        if (leftType.isSignedNumberType() != rightType.isSignedNumberType()) {
            // Signed with unsigned warning
        }

        if (leftType.sizeBytes() == rightType.sizeBytes()) {
            return leftType;
        }

        // promotion warning
        if (leftType.sizeBytes() < rightType.sizeBytes()) {
            return rightType;
        }

        return leftType;
    }


    public static Optional<Type> getResultType(Type leftType, Type rightType, BinaryOperationType operation) {
        if (leftType == Type.VOID || rightType == Type.VOID) {
            // Error
            return Optional.empty();
        }

        if (operation.isLogicalOperation()) {
            if (leftType == Type.BOOL && rightType == Type.BOOL) {
                return Optional.of(Type.BOOL);
            }
            // Error
            return Optional.empty();
        }

        if (operation.isComparisonOperation()) {
            return getComparisonResultType(leftType, rightType, operation);
        }

        return switch (operation) {
            case ADD, SUB, MULT, DIV, MOD -> getArithmeticResultType(leftType, rightType, operation);
            case BITWISE_AND, BITWISE_OR, BITWISE_XOR -> getBitwiseResultType(leftType, rightType);
            default -> Optional.empty();
        };
    }

    private static Optional<Type> getBitwiseResultType(Type leftType, Type rightType) {
        if (!leftType.isIntegerType() || !rightType.isIntegerType()) {
            // Error
            return Optional.empty();
        }

        return Optional.of(getIntegerResultType(leftType, rightType));
    }

    private static Optional<Type> getArithmeticResultType(Type leftType, Type rightType, BinaryOperationType operation) {
        if (!leftType.isNumberType() || !rightType.isNumberType()) {
            return Optional.empty();
        }

        if (operation == BinaryOperationType.MOD && (leftType.isFloatingPointType() || rightType.isFloatingPointType())) {
            return Optional.empty();
        }

        Type result = getNumberResultType(leftType, rightType);
        return Optional.of(result);
    }

    private static Optional<Type> getComparisonResultType(Type leftType, Type rightType, BinaryOperationType operation) {
        if (operation.isEqualityOperation()) {
            if (leftType == Type.BOOL && rightType == Type.BOOL) {
                return Optional.of(Type.BOOL);
            }

            if (leftType.isNumberType() && rightType.isNumberType()) {
                checkNumberConversions(leftType, rightType);
                return Optional.of(Type.BOOL);
            }

            return Optional.empty();
        }

        if (!leftType.isNumberType() || rightType.isNumberType()) {
            return Optional.empty();
        }

        checkNumberConversions(leftType, rightType);
        return Optional.of(Type.BOOL);
    }

    private static void checkNumberConversions(Type leftType, Type rightType) {
        getNumberResultType(leftType, rightType);
    }

}