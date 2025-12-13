package main.analysis;

import main.analysis.nodes.AnalyzedFunctionDeclaration;
import main.analysis.nodes.AnalyzedProgram;
import main.analysis.nodes.expressions.*;
import main.analysis.nodes.statements.*;
import main.errors.*;
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

public class Analyzer {
    private final Program program;
    private final List<SrcCodeError> errors;

    private int nextLocalVariableId;
    private List<LocalVariable> localVariables;

    public Analyzer(Program program) {
        this.program = program;
        this.errors = new ArrayList<>();
    }

    /**
     * Runs semantic analysis on the Program AST passed into the constructor
     * @return An analyzed AST if the analysis had no errors, otherwise, the errors
     * causing the AST to be invalid
     */
    public AnalysisResult runAnalysis() {
        var analyzed = analyzeProgram();
        return new AnalysisResult(analyzed, errors);
    }

    private AnalyzedProgram analyzeProgram() {
        Scope globalScope = new Scope(null);
        for (var function : program.functionDeclarations()) {
            if (globalScope.hasFunction(function.name())) {
                errors.add(new DuplicateFunctionNameError(function));
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
                errors.add(new DuplicateVariableNameError(parameter));
                continue;
            }
            functionScope.addVariable(parameter);
            addLocalVariableFrom(parameter);
        }

        Type returnType = functionDeclaration.returnType();
        var analyzed = analyzeCodeBlock(functionDeclaration.body(), functionScope, returnType);
        if (returnType != Type.VOID && !analyzed.hasGuaranteedReturn()) {
            errors.add(new IndeterminateReturnError(functionDeclaration));
        }
        return AnalyzedFunctionDeclaration.from(functionDeclaration, analyzed, localVariables);
    }

    private AnalyzedCodeBlock analyzeCodeBlock(CodeBlock codeBlock, Scope parentScope, Type functionReturnType) {
        final Scope thisScope = new Scope(parentScope);
        List<AnalyzedStatement> statements = new ArrayList<>();
        boolean hasGuaranteedReturn = false;
        for (var statement : codeBlock.statements()) {
            if (hasGuaranteedReturn) {
                errors.add(new UnreachableStatementError(statement));
                analyzeStatement(statement, thisScope, functionReturnType);
                continue;
            }
            var analyzed = analyzeStatement(statement, thisScope, functionReturnType);
            statements.add(analyzed);
            hasGuaranteedReturn = analyzed.hasGuaranteedReturn();
        }
        return new AnalyzedCodeBlock(statements, hasGuaranteedReturn, codeBlock.sourceInfo());
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
        return new AnalyzedBooleanLiteral(value, booleanLiteral.sourceInfo());
    }

    private AnalyzedVariableExpression analyzeVariableExpression(VariableExpression variableExpression, Scope scope) {
        if (!scope.hasVariable(variableExpression.name())) {
            errors.add(new UndefinedVariableError(variableExpression));
            return new AnalyzedVariableExpression(variableExpression.name(), Type.VOID, variableExpression.sourceInfo());
        }

        VariableSymbol target = scope.getVariable(variableExpression.name());
        return new AnalyzedVariableExpression(variableExpression.name(), target.type(), variableExpression.sourceInfo());
    }

    private AnalyzedUnaryOperation analyzeUnaryOperation(UnaryOperation unaryOperation, Scope scope) {
        var operation = unaryOperation.operation();
        var analyzedOperand = analyzeExpression(unaryOperation.operand(), scope);
        if (!UnaryOperation.isValid(analyzedOperand.resultType(), operation)) {
            errors.add(new InvalidOperationError(unaryOperation, analyzedOperand.resultType()));
        }
        return new AnalyzedUnaryOperation(operation, analyzedOperand, analyzedOperand.resultType(), unaryOperation.sourceInfo());
    }


    private AnalyzedIntegerLiteral analyzeIntegerLiteral(IntegerLiteral integerLiteral) {
        String literal = integerLiteral.value().toUpperCase();
        String suffixRemoved = literal.split("[LUSB]", 1)[0];
        BigInteger exactValue = new BigInteger(suffixRemoved);
        if (literal.contains("U")) {
            return switch (literal.charAt(literal.length() - 1)) {
                case 'L' -> {
                    if (exactValue.compareTo(Limits.ULONG_MAX_VALUE) > 0 || exactValue.compareTo(Limits.ULONG_MIN_VALUE) < 0) {
                        errors.add(new LiteralOverflowError(integerLiteral, Type.ULONG));
                    }
                    long value = Long.parseUnsignedLong(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.ULONG, integerLiteral.sourceInfo());
                }
                case 'U' -> {
                    if (exactValue.compareTo(Limits.UINT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.UINT_MIN_VALUE) < 0) {
                        errors.add(new LiteralOverflowError(integerLiteral, Type.UINT));
                    }

                    int value = Integer.parseUnsignedInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.UINT, integerLiteral.sourceInfo());
                }
                case 'S' -> {
                    if (exactValue.compareTo(Limits.USHORT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.USHORT_MIN_VALUE) < 0) {
                        errors.add(new LiteralOverflowError(integerLiteral, Type.USHORT));
                    }

                    short value = (short) Integer.parseInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.USHORT, integerLiteral.sourceInfo());
                }
                case 'B' -> {
                    if (exactValue.compareTo(Limits.UBYTE_MAX_VALUE) > 0 || exactValue.compareTo(Limits.UBYTE_MIN_VALUE) < 0) {
                        errors.add(new LiteralOverflowError(integerLiteral, Type.UBYTE));
                    }

                    byte value = (byte) Integer.parseInt(suffixRemoved);
                    yield new AnalyzedIntegerLiteral(value, Type.UBYTE, integerLiteral.sourceInfo());
                }
                default -> throw new IllegalStateException("Unexpected character in integer literal");
            };
        }

        return switch (literal.charAt(literal.length() - 1)) {
            case 'L' -> {
                if (exactValue.compareTo(Limits.LONG_MAX_VALUE) > 0 || exactValue.compareTo(Limits.LONG_MIN_VALUE) < 0) {
                    errors.add(new LiteralOverflowError(integerLiteral, Type.LONG));
                }
                long value = Long.parseLong(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.LONG, integerLiteral.sourceInfo());
            }
            case 'S' -> {
                if (exactValue.compareTo(Limits.SHORT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.SHORT_MIN_VALUE) < 0) {
                    errors.add(new LiteralOverflowError(integerLiteral, Type.SHORT));
                }

                short value = Short.parseShort(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.SHORT, integerLiteral.sourceInfo());
            }
            case 'B' -> {
                if (exactValue.compareTo(Limits.BYTE_MAX_VALUE) > 0 || exactValue.compareTo(Limits.BYTE_MIN_VALUE) < 0) {
                    errors.add(new LiteralOverflowError(integerLiteral, Type.BYTE));
                }

                byte value = Byte.parseByte(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.BYTE, integerLiteral.sourceInfo());
            }
            default -> {
                if (exactValue.compareTo(Limits.INT_MAX_VALUE) > 0 || exactValue.compareTo(Limits.INT_MIN_VALUE) < 0) {
                    errors.add(new LiteralOverflowError(integerLiteral, Type.INT));
                }

                int value = Integer.parseInt(suffixRemoved);
                yield new AnalyzedIntegerLiteral(value, Type.INT, integerLiteral.sourceInfo());
            }
        };
    }

    private AnalyzedFunctionCall analyzeFunctionCall(FunctionCall functionCall, Scope scope) {
        if (!scope.hasFunction(functionCall.name())) {
            errors.add(new UndefinedFunctionError(functionCall));
            return new AnalyzedFunctionCall(functionCall.name(), List.of(), Type.VOID, functionCall.sourceInfo());
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
            errors.add(new ArgumentTypeMismatchError(functionCall, argumentTypes, target));
        }

        return new AnalyzedFunctionCall(functionCall.name(), analyzedArguments, target.returnType(), functionCall.sourceInfo());
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
                    errors.add(new LiteralOverflowError(floatingPointLiteral,  Type.FLOAT));
                }

                float value = Float.parseFloat(suffixRemoved);
                yield new AnalyzedFloatingPointLiteral(value, Type.FLOAT, floatingPointLiteral.sourceInfo());
            }
            case DOUBLE -> {
                if (exactValue.abs().compareTo(Limits.DOUBLE_MAX_VALUE) > 0 || exactValue.abs().compareTo(Limits.DOUBLE_MIN_VALUE) < 0) {
                    errors.add(new LiteralOverflowError(floatingPointLiteral,  Type.DOUBLE));
                }

                double value = Double.parseDouble(suffixRemoved);
                yield new AnalyzedFloatingPointLiteral(value, Type.FLOAT, floatingPointLiteral.sourceInfo());
            }
            default -> throw new IllegalStateException("Unexpected type of floating point literal");
        };
    }

    private AnalyzedBinaryOperation analyzeBinaryOperation(BinaryOperation binaryOperation, Scope scope) {
        var analyzedLeft = analyzeExpression(binaryOperation.left(), scope);
        var analyzedRight = analyzeExpression(binaryOperation.right(), scope);
        BinaryOperation.BinaryOperationType operation = binaryOperation.operation();
        Optional<Type> type = BinaryOperation.getResultType(analyzedLeft.resultType(), analyzedRight.resultType(), operation);
        // TODO: extract warnings/errors from above method
        if (type.isEmpty()) {
            errors.add(new InvalidOperationError(binaryOperation, analyzedLeft.resultType(), analyzedRight.resultType()));
        }
        var resultType = type.orElse(null);
        return new AnalyzedBinaryOperation(operation, analyzedLeft, analyzedRight, resultType, binaryOperation.sourceInfo());
    }

    private AnalyzedAssignment analyzeAssignment(Assignment assignment, Scope scope) {
        if (!scope.hasVariable(assignment.variableName())) {
            errors.add(new UndefinedVariableError(assignment));
            var analyzedValue = analyzeExpression(assignment.value(), scope);
            return new AnalyzedAssignment(assignment.variableName(), analyzedValue, assignment.sourceInfo());
        }

        VariableSymbol target = scope.getVariable(assignment.variableName());

        var analyzedValue = analyzeExpression(assignment.value(), scope);

        if (target.type() != analyzedValue.resultType()) {
            errors.add(new TypeMismatchError(assignment, target.type(), analyzedValue.resultType()));
        }

        return new AnalyzedAssignment(assignment.variableName(), analyzedValue, assignment.sourceInfo());
    }

    private AnalyzedVariableDeclaration analyzeVariableDeclaration(VariableDeclaration variableDeclaration, Scope scope) {
        if (scope.hasVariable(variableDeclaration.name())) {
            errors.add(new DuplicateVariableNameError(variableDeclaration));
            return AnalyzedVariableDeclaration.from(variableDeclaration, Optional.empty());
        }

        if (variableDeclaration.type() == Type.VOID) {
            errors.add(new VoidVariableError(variableDeclaration));
            return AnalyzedVariableDeclaration.from(variableDeclaration, Optional.empty());
        }

        scope.addVariable(variableDeclaration);
        addLocalVariableFrom(variableDeclaration);

        AnalyzedExpression analyzedInitialValue = null;
        if (variableDeclaration.initialValue().isPresent()) {
            analyzedInitialValue = analyzeExpression(variableDeclaration.initialValue().get(), scope);
            if (variableDeclaration.type() != analyzedInitialValue.resultType()) {
                errors.add(new TypeMismatchError(variableDeclaration, analyzedInitialValue.resultType()));
            }
        }

        return AnalyzedVariableDeclaration.from(variableDeclaration, Optional.ofNullable(analyzedInitialValue));
    }

    private AnalyzedReturnStatement analyzeReturnStatement(ReturnStatement returnStatement, Scope scope, Type functionReturnType) {
        AnalyzedExpression analyzedReturnValue = null;
        if (returnStatement.value().isPresent()) {
            if (functionReturnType == Type.VOID) {
                errors.add(new ReturnTypeError(returnStatement));
                return new AnalyzedReturnStatement(Optional.empty(), returnStatement.sourceInfo());
            }
            analyzedReturnValue = analyzeExpression(returnStatement.value().get(), scope);
            if (analyzedReturnValue.resultType() != functionReturnType) {
                errors.add(new ReturnTypeError(returnStatement, functionReturnType, analyzedReturnValue.resultType()));
            }
        } else if (functionReturnType != Type.VOID) {
            errors.add(new ReturnMissingValueError(returnStatement));
        }

        return new AnalyzedReturnStatement(Optional.ofNullable(analyzedReturnValue), returnStatement.sourceInfo());
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
                errors.add(new InvalidConditionError(forStatement));
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
            hasGuaranteedReturn,
            forStatement.sourceInfo()
        );
    }

    private AnalyzedDoWhileStatement analyzeDoWhileStatement(DoWhileStatement doWhileStatement, Scope scope, Type functionReturnType) {
        var analyzedBody = analyzeStatement(doWhileStatement.body(), scope, functionReturnType);

        var analyzedCondition = analyzeExpression(doWhileStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new InvalidConditionError(doWhileStatement));
        }

        return new AnalyzedDoWhileStatement(analyzedBody, analyzedCondition, analyzedBody.hasGuaranteedReturn(), doWhileStatement.sourceInfo());
    }


    private AnalyzedWhileStatement analyzeWhileStatement(WhileStatement whileStatement, Scope scope, Type functionReturnType) {
        var analyzedCondition = analyzeExpression(whileStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new InvalidConditionError(whileStatement));
        }

        var alwaysTrue = whileStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var analyzedBody = analyzeStatement(whileStatement.body(), scope, functionReturnType);

        var hasGuaranteedReturn = alwaysTrue && analyzedBody.hasGuaranteedReturn();
        return new AnalyzedWhileStatement(analyzedCondition, analyzedBody, hasGuaranteedReturn, whileStatement.sourceInfo());
    }

    private AnalyzedIfStatement analyzeIfStatement(IfStatement ifStatement, Scope scope, Type functionReturnType) {
        var analyzedCondition = analyzeExpression(ifStatement.condition(), scope);
        if (analyzedCondition.resultType() != Type.BOOL) {
            errors.add(new InvalidConditionError(ifStatement));
        }

        var alwaysTrue = ifStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var analyzedBody = analyzeStatement(ifStatement.body(), scope, functionReturnType);

        if (ifStatement.elseBody().isPresent()) {
            var analyzedElseBody = analyzeStatement(ifStatement.elseBody().get(), scope, functionReturnType);
            var hasGuaranteedReturn = analyzedBody.hasGuaranteedReturn() && (alwaysTrue || analyzedElseBody.hasGuaranteedReturn());
            return new AnalyzedIfStatement(analyzedCondition, analyzedBody, Optional.of(analyzedElseBody), hasGuaranteedReturn, ifStatement.sourceInfo());
        }

        boolean hasGuaranteedReturn = alwaysTrue && analyzedBody.hasGuaranteedReturn();
        return new AnalyzedIfStatement(analyzedCondition, analyzedBody, Optional.empty(), hasGuaranteedReturn, ifStatement.sourceInfo());
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
