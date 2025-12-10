package main.analysis;

import main.analysis.nodes.AnalyzedFunctionDeclaration;
import main.analysis.nodes.AnalyzedProgram;
import main.analysis.nodes.expressions.*;
import main.analysis.nodes.statements.*;
import main.errors.SemanticError;
import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Parameter;
import main.parser.nodes.Program;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.*;
import main.parser.nodes.expressions.BooleanLiteral;
import main.parser.nodes.expressions.FloatingPointLiteral;
import main.parser.nodes.expressions.IntegerLiteral;
import main.parser.nodes.statements.*;
import main.parser.nodes.statements.Assignment;
import main.parser.nodes.statements.ForStatement;
import main.parser.nodes.statements.ReturnStatement;
import main.parser.nodes.statements.VariableDeclaration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static main.util.StringUtils.typeListAsString;

public class Analyzer {
    private final Program program;
    private final List<SemanticError> errors;

    private int nextLocalVariableId;
    private List<LocalVariable> localVariables;

    public Analyzer(Program program) {
        this.program = program;
        this.errors = new ArrayList<>();
    }

    public AnalysisResult runAnalysis() {
        var analyzed = analyzeProgram();
        return new AnalysisResult(analyzed, errors);
    }

    private AnalyzedProgram analyzeProgram() {
        Scope globalScope = new Scope(null);
        for (var function : program.functionDeclarations()) {
            if (globalScope.hasFunction(function.name())) {
                errors.add(new SemanticError("Function with name '" + function.name() + "' conflicts with another function in scope"));
                continue;
            }
            globalScope.addFunction(function);
        }

        List<AnalyzedFunctionDeclaration> analyzedFunctionDeclarations = new ArrayList<>();

        for (var function : program.functionDeclarations()) {
            var analyzed = analyzeFunctionDeclaration(function, globalScope);
            analyzedFunctionDeclarations.add(analyzed);
        }

        return new AnalyzedProgram(analyzedFunctionDeclarations);
    }

    private AnalyzedFunctionDeclaration analyzeFunctionDeclaration(FunctionDeclaration functionDeclaration, Scope globalScope) {
        final Scope functionScope = new Scope(globalScope);
        nextLocalVariableId = 0;
        localVariables = new ArrayList<>();
        for (var parameter : functionDeclaration.parameters()) {
            if (functionScope.hasVariable(parameter.name())) {
                errors.add(new SemanticError("Parameter with name '" + parameter.name() + "' conflicts with another variable in scope"));
                continue;
            }
            functionScope.addVariable(parameter);
            addLocalVariableFrom(parameter);
        }

        Type returnType = functionDeclaration.returnType();
        var analyzed = analyzeCodeBlock(functionDeclaration.body(), functionScope, returnType);
        if (returnType != Type.VOID && !analyzed.hasGuaranteedReturn()) {
            errors.add(new SemanticError("Not all code paths return a value"));
        }
        return AnalyzedFunctionDeclaration.from(functionDeclaration, analyzed, localVariables);
    }

    private AnalyzedCodeBlock analyzeCodeBlock(CodeBlock codeBlock, Scope parentScope, Type functionReturnType) {
        final Scope thisScope = new Scope(parentScope);
        List<AnalyzedStatement> statements = new ArrayList<>();
        boolean hasGuaranteedReturn = false;
        for (var statement : codeBlock.statements()) {
            if (hasGuaranteedReturn) {
                errors.add(new SemanticError("Unreachable statement")); // TODO: carry source information to show where
                analyzeStatement(statement, thisScope, functionReturnType);
                continue;
            }
            var analyzed = analyzeStatement(statement, thisScope, functionReturnType);
            statements.add(analyzed);
            hasGuaranteedReturn = analyzed.hasGuaranteedReturn();
        }
        return new AnalyzedCodeBlock(statements, hasGuaranteedReturn);
    }

    private AnalyzedStatement analyzeStatement(Statement statement, Scope scope, Type functionReturnType) {
        return switch (statement) {
            case CodeBlock           nestedCodeBlock     -> analyzeCodeBlock(nestedCodeBlock, scope, functionReturnType);
            case IfStatement         ifStatement         -> analyzeIfStatement(ifStatement, scope, functionReturnType);
            case WhileStatement      whileStatement      -> analyzeWhileStatement(whileStatement, scope, functionReturnType);
            case DoWhileStatement    doWhileStatement    -> analyzeDoWhileStatement(doWhileStatement, scope, functionReturnType);
            case ForStatement        forStatement        -> analyzeForStatement(forStatement, scope, functionReturnType);
            case ReturnStatement     returnStatement     -> analyzeReturnStatement(returnStatement, scope, functionReturnType);
            case VariableDeclaration variableDeclaration -> analyzeVariableDeclaration(variableDeclaration, scope);
            case Assignment          assignment          -> analyzeAssignment(assignment, scope);
        };
    }

    private AnalyzedExpression analyzeExpression(Expression expression, Scope scope) {
        return switch (expression) {
            case FunctionCall         functionCall         -> analyzeFunctionCall(functionCall, scope);
            case BinaryOperation      binaryOperation      -> analyzeBinaryOperation(binaryOperation, scope);
            case UnaryOperation       unaryOperation       -> analyzeUnaryOperation(unaryOperation, scope);
            case VariableExpression   variableExpression   -> analyzeVariableExpression(variableExpression, scope);
            case FloatingPointLiteral floatingPointLiteral -> analyzeFloatingPointLiteral(floatingPointLiteral);
            case IntegerLiteral       integerLiteral       -> analyzeIntegerLiteral(integerLiteral);
            case BooleanLiteral       booleanLiteral       -> analyzeBooleanLiteral(booleanLiteral);
        };
    }

    private AnalyzedBooleanLiteral analyzeBooleanLiteral(BooleanLiteral booleanLiteral) {
        boolean value = booleanLiteral.value() == BooleanLiteral.Value.TRUE;
        return new AnalyzedBooleanLiteral(value);
    }

    private AnalyzedVariableExpression analyzeVariableExpression(VariableExpression variableExpression, Scope scope) {
        if (!scope.hasVariable(variableExpression.name())) {
            errors.add(new SemanticError("Variable with name '" + variableExpression.name() + "' is not defined in scope"));
            return new AnalyzedVariableExpression(variableExpression.name(), Type.VOID);
        }

        VariableSymbol target = scope.getVariable(variableExpression.name());
        return new AnalyzedVariableExpression(variableExpression.name(), target.type());
    }

    private AnalyzedUnaryOperation analyzeUnaryOperation(UnaryOperation unaryOperation, Scope scope) {
        var operation = unaryOperation.operation();
        var analyzedOperand = analyzeExpression(unaryOperation.operand(), scope);
        if (!isValidUnaryOperation(analyzedOperand.resultType(), operation)) {
            errors.add(new SemanticError("Operator '" + operation + "' is not valid on type '" + analyzedOperand + "'"));
        }
        return new AnalyzedUnaryOperation(operation, analyzedOperand, analyzedOperand.resultType());
    }

    private boolean isValidUnaryOperation(Type operandType, UnaryOperation.UnaryOperationType operation) {
        return switch (operation) {
            case NEGATION -> operandType.isSignedNumberType();
            case BITWISE_NOT -> operandType.isIntegerType();
            case LOGICAL_NOT -> operandType == Type.BOOL;
        };
    }

    private AnalyzedIntegerLiteral analyzeIntegerLiteral(IntegerLiteral integerLiteral) {
        String literal = integerLiteral.value().toUpperCase();
        String suffixRemoved = literal.split("[LUSB]", 1)[0];
        BigInteger exactValue = new BigInteger(suffixRemoved);
        if (literal.contains("U")) {
            return switch (literal.charAt(literal.length() - 1)) {
                case 'L' -> {
                    if (exactValue.compareTo(Limits.ULONG_MAX_VALUE) > 0 || exactValue.compareTo(Limits.ULONG_MIN_VALUE) < 0) {
                        errors.add(new SemanticError("Value does not fit in 'ulong' data type"));
                    }
                    long value = Long.parseUnsignedLong(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.ULONG);
                }
                case 'U' -> {
                    if (exactValue.compareTo(Limits.UINT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.UINT_MIN_VALUE) < 0) {
                        errors.add(new SemanticError("Value does not fit in 'uint' data type"));
                    }

                    int value = Integer.parseUnsignedInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.UINT);
                }
                case 'S' -> {
                    if (exactValue.compareTo(Limits.USHORT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.USHORT_MIN_VALUE) < 0) {
                        errors.add(new SemanticError("Value does not fit in 'ushort' data type"));
                    }

                    short value = (short) Integer.parseInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.USHORT);
                }
                case 'B' -> {
                    if (exactValue.compareTo(Limits.UBYTE_MAX_VALUE) > 0 || exactValue.compareTo(Limits.UBYTE_MIN_VALUE) < 0) {
                        errors.add(new SemanticError("Value does not fit in 'ubyte' data type"));
                    }

                    byte value = (byte) Integer.parseInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.UBYTE);
                }
                default -> throw new IllegalStateException("Unexpected character in integer literal");
            };
        }

        return switch (literal.charAt(literal.length() - 1)) {
            case 'L' -> {
                if (exactValue.compareTo(Limits.LONG_MAX_VALUE) > 0 || exactValue.compareTo(Limits.LONG_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'long' data type"));
                }
                long value = Long.parseLong(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.LONG);
            }
            case 'S' -> {
                if (exactValue.compareTo(Limits.SHORT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.SHORT_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'short' data type"));
                }

                short value = Short.parseShort(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.SHORT);
            }
            case 'B' -> {
                if (exactValue.compareTo(Limits.BYTE_MAX_VALUE) > 0 || exactValue.compareTo(Limits.BYTE_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'byte' data type"));
                }

                byte value = Byte.parseByte(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.BYTE);
            }
            default -> {
                if (exactValue.compareTo(Limits.INT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.INT_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'int' data type"));
                }

                int value = Integer.parseInt(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.INT);
            }
        };
    }

    private AnalyzedFunctionCall analyzeFunctionCall(FunctionCall functionCall, Scope scope) {
        if (!scope.hasFunction(functionCall.name())) {
            errors.add(new SemanticError("Function with name '" + functionCall.name() + "' is not defined in scope"));
            return new AnalyzedFunctionCall(functionCall.name(), List.of(), Type.VOID);
        }

        FunctionSymbol target = scope.getFunction(functionCall.name());

        List<Type> parameterTypes = target.parameterTypes();
        List<AnalyzedExpression> analyzedArguments = new ArrayList<>();
        List<Type> argumentTypes = new ArrayList<>();
        for (Expression argument : functionCall.arguments()) {
            var analyzedArgument = analyzeExpression(argument, scope);
            analyzedArguments.add(analyzedArgument);
            argumentTypes.add(analyzedArgument.resultType());
        }

        if (!argumentTypes.equals(parameterTypes)) {
            errors.add(new SemanticError(
                """
                Argument type mismatch for function '%s'
                Expected: %s
                Actual  : %s(%s)
                """
                .formatted(
                    functionCall.name(),
                    target.signatureString(),
                    functionCall.name(),
                    typeListAsString(argumentTypes)
                )
            ));
        }

        return new AnalyzedFunctionCall(functionCall.name(), analyzedArguments, target.returnType());
    }

    private AnalyzedFloatingPointLiteral analyzeFloatingPointLiteral(FloatingPointLiteral floatingPointLiteral) {
        String literal = floatingPointLiteral.value();
        Type type = switch (literal.charAt(literal.length() - 1)) {
            case 'f', 'F' -> Type.FLOAT;
            default -> Type.DOUBLE;
        };
        String suffixRemoved = literal.split("[dDfF]", 1)[0];

        var exactValue = new BigDecimal(suffixRemoved);
        return switch (type) {
            case FLOAT -> {
                if (exactValue.abs().compareTo(Limits.FLOAT_MAX_VALUE) > 0 || exactValue.abs().compareTo(Limits.FLOAT_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'float' data type"));
                }

                float value = Float.parseFloat(suffixRemoved);
                yield new AnalyzedFloatingPointLiteral(value, Type.FLOAT);
            }
            case DOUBLE -> {
                if (exactValue.abs().compareTo(Limits.DOUBLE_MAX_VALUE) > 0 || exactValue.abs().compareTo(Limits.DOUBLE_MIN_VALUE) < 0) {
                    errors.add(new SemanticError("Value does not fit in 'double' data type"));
                }

                double value = Double.parseDouble(suffixRemoved);
                yield new AnalyzedFloatingPointLiteral(value, Type.FLOAT);
            }
            default -> throw new IllegalStateException("Unexpected type of floating point literal");
        };
    }

    private AnalyzedBinaryOperation analyzeBinaryOperation(BinaryOperation binaryOperation, Scope scope) {
        var analyzedLeft = analyzeExpression(binaryOperation.left(), scope);
        var analyzedRight = analyzeExpression(binaryOperation.right(), scope);
        BinaryOperation.BinaryOperationType operation = binaryOperation.operation();
        Optional<Type> type = BinaryOperation.getResultType(analyzedLeft.resultType(), analyzedRight.resultType(), operation);
        // TODO: extract warnings
        var resultType = type.orElse(null);
        return new AnalyzedBinaryOperation(operation, analyzedLeft, analyzedRight, resultType);
    }

    private AnalyzedAssignment analyzeAssignment(Assignment assignment, Scope scope) {
        if (!scope.hasVariable(assignment.variableName())) {
            errors.add(new SemanticError("Could not find variable with name '" + assignment.variableName() + "'"));
            var analyzedValue = analyzeExpression(assignment.value(), scope);
            return new AnalyzedAssignment(assignment.variableName(), analyzedValue);
        }

        VariableSymbol target = scope.getVariable(assignment.variableName());

        var analyzedValue = analyzeExpression(assignment.value(), scope);

        if (target.type() != analyzedValue.resultType()) {
            errors.add(new SemanticError("Variable '" + target.name() + "' with type '" + target.type() + "' assigned to value of type '" + analyzedValue.resultType() + "'"));
        }

        return new AnalyzedAssignment(assignment.variableName(), analyzedValue);
    }

    private AnalyzedVariableDeclaration analyzeVariableDeclaration(VariableDeclaration variableDeclaration, Scope scope) {
        if (scope.hasVariable(variableDeclaration.name())) {
            errors.add(new SemanticError("Variable with name '" + variableDeclaration.name() + "' conflicts with another variable in scope"));
            return new AnalyzedVariableDeclaration(variableDeclaration.type(), variableDeclaration.name(), Optional.empty());
        }

        if (variableDeclaration.type() == Type.VOID) {
            errors.add(new SemanticError("Variable cannot be declared as void type"));
            return new AnalyzedVariableDeclaration(variableDeclaration.type(), variableDeclaration.name(), Optional.empty());
        }

        scope.addVariable(variableDeclaration);
        addLocalVariableFrom(variableDeclaration);

        AnalyzedExpression analyzedInitialValue = null;
        if (variableDeclaration.initialValue().isPresent()) {
            analyzedInitialValue = analyzeExpression(variableDeclaration.initialValue().get(), scope);
            if (variableDeclaration.type() != analyzedInitialValue.resultType()) {
                errors.add(new SemanticError("Variable '" + variableDeclaration.name() + "' declared with type '" + variableDeclaration.type() + "' but assigned to value of type '" + analyzedInitialValue.resultType() + "'"));
            }
        }

        return new AnalyzedVariableDeclaration(
            variableDeclaration.type(),
            variableDeclaration.name(),
            Optional.ofNullable(analyzedInitialValue)
        );
    }

    private AnalyzedReturnStatement analyzeReturnStatement(ReturnStatement returnStatement, Scope scope, Type functionReturnType) {
        AnalyzedExpression analyzedReturnValue = null;
        if (returnStatement.value().isPresent()) {
            if (functionReturnType == Type.VOID) {
                errors.add(new SemanticError("Returned a value in void function"));
                return new AnalyzedReturnStatement(Optional.empty());
            }
            analyzedReturnValue = analyzeExpression(returnStatement.value().get(), scope);
            if (analyzedReturnValue.resultType() != functionReturnType) {
                errors.add(new SemanticError("Returned value of type '" + analyzedReturnValue.resultType() + "' in function with return type of '" + functionReturnType + "'"));
            }
        } else if (functionReturnType != Type.VOID) {
            errors.add(new SemanticError("Return value expected"));
        }

        return new AnalyzedReturnStatement(Optional.ofNullable(analyzedReturnValue));
    }

    private AnalyzedForStatement analyzeForStatement(ForStatement forStatement, Scope scope, Type functionReturnType) {
        final Scope headerScope = new Scope(scope);
        AnalyzedForStatement.Initializer analyzedInitializer = null;
        if (forStatement.initializer().isPresent()) {
            analyzedInitializer = switch (forStatement.initializer().get()) {
                case VariableDeclaration initVariable -> analyzeVariableDeclaration(initVariable, headerScope);
                case Assignment          assignment   -> analyzeAssignment(assignment, headerScope);
            };
        }

        var alwaysTrue = forStatement.condition().isEmpty();

        AnalyzedExpression analyzedCondition = null;
        if (forStatement.condition().isPresent()) {
            analyzedCondition = analyzeExpression(forStatement.condition().get(), headerScope);
            if (analyzedCondition.resultType() != Type.BOOL) {
                errors.add(new SemanticError("For loop contains non-boolean condition"));
            }
            alwaysTrue = forStatement.condition().get() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;
        }

        AnalyzedAssignment analyzedAssignment = null;
        if (forStatement.update().isPresent()) {
            analyzedAssignment = analyzeAssignment(forStatement.update().get(), headerScope);
        }

        var analyzedBody = analyzeStatement(forStatement.body(), headerScope, functionReturnType);
        
        boolean hasGuaranteedReturn = alwaysTrue && analyzedBody.hasGuaranteedReturn();

        return new AnalyzedForStatement(
            Optional.ofNullable(analyzedInitializer),
            Optional.ofNullable(analyzedCondition),
            Optional.ofNullable(analyzedAssignment), 
            analyzedBody,
            hasGuaranteedReturn        
        );
    }

    private AnalyzedDoWhileStatement analyzeDoWhileStatement(DoWhileStatement doWhileStatement, Scope scope, Type functionReturnType) {
        var analyzedBody = analyzeStatement(doWhileStatement.body(), scope, functionReturnType);

        var analyzedCondition = analyzeExpression(doWhileStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new SemanticError("Do-While loop contains non-boolean condition"));
        }

        return new AnalyzedDoWhileStatement(analyzedBody, analyzedCondition, analyzedBody.hasGuaranteedReturn());
    }


    private AnalyzedWhileStatement analyzeWhileStatement(WhileStatement whileStatement, Scope scope, Type functionReturnType) {
        var analyzedCondition = analyzeExpression(whileStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new SemanticError("While loop contains non-boolean condition"));
        }

        var alwaysTrue = whileStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var analyzedBody = analyzeStatement(whileStatement.body(), scope, functionReturnType);

        var hasGuaranteedReturn = alwaysTrue && analyzedBody.hasGuaranteedReturn();
        return new AnalyzedWhileStatement(analyzedCondition, analyzedBody, hasGuaranteedReturn);
    }

    private AnalyzedIfStatement analyzeIfStatement(IfStatement ifStatement, Scope scope, Type functionReturnType) {
        var analyzedCondition = analyzeExpression(ifStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new SemanticError("If statement contains non-boolean condition"));
        }

        var alwaysTrue = ifStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var analyzedBody = analyzeStatement(ifStatement.body(), scope, functionReturnType);

        if (ifStatement.elseBody().isPresent()) {
            var analyzedElseBody = analyzeStatement(ifStatement.elseBody().get(), scope, functionReturnType);
            var hasGuaranteedReturn = analyzedBody.hasGuaranteedReturn() && (alwaysTrue || analyzedElseBody.hasGuaranteedReturn());
            return new AnalyzedIfStatement(analyzedCondition, analyzedBody, Optional.of(analyzedElseBody),  hasGuaranteedReturn);
        }

        boolean hasGuaranteedReturn = alwaysTrue && analyzedBody.hasGuaranteedReturn();
        return new AnalyzedIfStatement(analyzedCondition, analyzedBody, Optional.empty(), hasGuaranteedReturn);
    }

    private void addLocalVariableFrom(VariableDeclaration variableDeclaration) {
        var localVariable = new LocalVariable(nextLocalVariableId, variableDeclaration.name(), variableDeclaration.type());
        nextLocalVariableId++;
        localVariables.add(localVariable);
    }

    private void addLocalVariableFrom(Parameter parameter) {
        var localVariable = new LocalVariable(nextLocalVariableId, parameter.name(), parameter.type());
        nextLocalVariableId++;
        localVariables.add(localVariable);
    }
}
