package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.analysis.errors.AnalysisError;
import luxlang.compiler.analysis.errors.InvalidOperationError;
import luxlang.compiler.analysis.errors.VoidOperandError;
import luxlang.compiler.analysis.warnings.AnalysisWarning;
import luxlang.compiler.analysis.warnings.IntegerPromotionWarning;
import luxlang.compiler.analysis.warnings.ImplicitConversionWarning;
import luxlang.compiler.analysis.warnings.SignednessMismatchWarning;
import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation;

import java.util.ArrayList;
import java.util.List;

public record AnalyzedBinaryOperation(
    BinaryOperation.BinaryOperationType operation,
    AnalyzedExpression left,
    AnalyzedExpression right,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression {
    public record TypeAnalysisResult(Type type, List<AnalysisError> errors, List<AnalysisWarning> warnings) { }

    private static TypeAnalysisResult getNumberResultType(Type leftType, Type rightType, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (leftType.isFloatingPointType() || rightType.isFloatingPointType()) {
            Type resultType = leftType == Type.DOUBLE || rightType == Type.DOUBLE ? Type.DOUBLE : Type.FLOAT;

            if (leftType != rightType) {
                warnings.add(new ImplicitConversionWarning(sourceInfo, leftType, rightType));
            }

            return new TypeAnalysisResult(resultType, errors, warnings);
        }
        return getIntegerResultType(leftType, rightType, sourceInfo);
    }

    public static TypeAnalysisResult getIntegerResultType(Type leftType, Type rightType, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (leftType.isSignedNumberType() != rightType.isSignedNumberType()) {
            warnings.add(new SignednessMismatchWarning(sourceInfo));
        }

        if (leftType.sizeBytes() == rightType.sizeBytes()) {
            return new TypeAnalysisResult(leftType, errors, warnings);
        }

        if (leftType.sizeBytes() < rightType.sizeBytes()) {
            warnings.add(new IntegerPromotionWarning(sourceInfo, leftType, rightType));
            return new TypeAnalysisResult(rightType, errors, warnings);
        }

        warnings.add(new IntegerPromotionWarning(sourceInfo, rightType, leftType));
        return new TypeAnalysisResult(leftType, errors, warnings);
    }

    public static TypeAnalysisResult getResultType(Type leftType, Type rightType, BinaryOperation.BinaryOperationType operation, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (leftType == Type.ERROR || rightType == Type.ERROR) {
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        if (leftType == Type.VOID || rightType == Type.VOID) {
            errors.add(new VoidOperandError(sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        if (operation.isLogicalOperation()) {
            if (leftType == Type.BOOL && rightType == Type.BOOL) {
                return new TypeAnalysisResult(Type.BOOL, errors, warnings);
            }
            errors.add(new InvalidOperationError(operation, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        if (operation.isComparisonOperation()) {
            return getComparisonResultType(leftType, rightType, operation, sourceInfo);
        }

        return switch (operation) {
            case ADD, SUB, MULT, DIV, MOD -> getArithmeticResultType(leftType, rightType, operation, sourceInfo);
            case BITWISE_AND, BITWISE_OR, BITWISE_XOR -> getBitwiseResultType(leftType, rightType, sourceInfo);
            default -> new TypeAnalysisResult(Type.ERROR, errors, warnings);
        };
    }

    private static TypeAnalysisResult getBitwiseResultType(Type leftType, Type rightType, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (!leftType.isIntegerType() || !rightType.isIntegerType()) {
            errors.add(new InvalidOperationError(BinaryOperation.BinaryOperationType.BITWISE_AND, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        var intResult = getIntegerResultType(leftType, rightType, sourceInfo);
        errors.addAll(intResult.errors());
        warnings.addAll(intResult.warnings());
        return new TypeAnalysisResult(intResult.type(), errors, warnings);
    }

    private static TypeAnalysisResult getArithmeticResultType(Type leftType, Type rightType, BinaryOperation.BinaryOperationType operation, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (!leftType.isNumberType() || !rightType.isNumberType()) {
            errors.add(new InvalidOperationError(operation, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        if (operation == BinaryOperation.BinaryOperationType.MOD && (leftType.isFloatingPointType() || rightType.isFloatingPointType())) {
            errors.add(new InvalidOperationError(operation, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        var result = getNumberResultType(leftType, rightType, sourceInfo);
        errors.addAll(result.errors());
        warnings.addAll(result.warnings());
        return new TypeAnalysisResult(result.type(), errors, warnings);
    }

    private static TypeAnalysisResult getComparisonResultType(Type leftType, Type rightType, BinaryOperation.BinaryOperationType operation, SourceInfo sourceInfo) {
        List<AnalysisError> errors = new ArrayList<>();
        List<AnalysisWarning> warnings = new ArrayList<>();

        if (operation.isEqualityOperation()) {
            if (leftType == Type.BOOL && rightType == Type.BOOL) {
                return new TypeAnalysisResult(Type.BOOL, errors, warnings);
            }

            if (leftType.isNumberType() && rightType.isNumberType()) {
                var numberResult = getNumberResultType(leftType, rightType, sourceInfo);
                warnings.addAll(numberResult.warnings());
                errors.addAll(numberResult.errors());
                return new TypeAnalysisResult(Type.BOOL, errors, warnings);
            }

            errors.add(new InvalidOperationError(operation, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        if (!leftType.isNumberType() || !rightType.isNumberType()) {
            errors.add(new InvalidOperationError(operation, leftType, rightType, sourceInfo));
            return new TypeAnalysisResult(Type.ERROR, errors, warnings);
        }

        var numberResult = getNumberResultType(leftType, rightType, sourceInfo);
        warnings.addAll(numberResult.warnings());
        errors.addAll(numberResult.errors());
        return new TypeAnalysisResult(Type.BOOL, errors, warnings);
    }
}