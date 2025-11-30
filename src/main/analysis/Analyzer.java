package main.analysis;

import main.errors.PermissibleError;
import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Program;
import main.parser.nodes.Type;
import main.parser.nodes.expressions.*;
import main.parser.nodes.statements.*;

import java.util.ArrayList;
import java.util.List;

import static main.util.StringUtils.typeListAsString;

public class Analyzer {
    private final Program program;
    private final List<PermissibleError> errors;

    public Analyzer(Program program) {
        this.program = program;
        this.errors = new ArrayList<>();
    }

    public List<PermissibleError> runAnalysis() {
        analyzeProgram();
        return errors;
    }

    private void analyzeProgram() {
        Scope globalScope = new Scope(null);
        for (var function : program.functionDeclarations()) {
            if (globalScope.hasFunction(function.name())) {
                errors.add(new PermissibleError("Function with name '" + function.name() + "' conflicts with another function in scope"));
                continue;
            }
            globalScope.addFunction(function);
        }

        for (var function : program.functionDeclarations()) {
            analyzeFunctionDeclaration(function, globalScope);
        }
    }

    private void analyzeFunctionDeclaration(FunctionDeclaration functionDeclaration, Scope scope) {
        final Scope functionScope = new Scope(scope);
        for (var parameter : functionDeclaration.parameters()) {
            if (functionScope.hasVariable(parameter.name())) {
                errors.add(new PermissibleError("Parameter with name '" + parameter.name() + "' conflicts with another variable in scope"));
                continue;
            }
            functionScope.addVariable(parameter);
        }

        Type returnType = functionDeclaration.returnType();
        var analysisResult = analyzeCodeBlock(functionDeclaration.body(), functionScope, returnType);
        if (returnType == Type.VOID) {
            return;
        }
        if (!analysisResult.hasGuaranteedReturn()) {
            errors.add(new PermissibleError("Not all code paths return a value"));
        }

    }

    private StatementAnalysisResult analyzeCodeBlock(CodeBlock codeBlock, Scope parentScope, Type functionReturnType) {
        final Scope thisScope = new Scope(parentScope);
        boolean guaranteedReturn = false;
        for (var statement : codeBlock.statements()) {
            if (guaranteedReturn) {
                errors.add(new PermissibleError("Unreachable statement"));
                analyzeStatement(statement, thisScope, functionReturnType);
                continue;
            }
            var analysisResult = analyzeStatement(statement, thisScope, functionReturnType);
            guaranteedReturn = analysisResult.hasGuaranteedReturn();
        }
        return new StatementAnalysisResult(guaranteedReturn);
    }

    private StatementAnalysisResult analyzeStatement(Statement statement, Scope scope, Type functionReturnType) {
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

    private boolean isValidBinaryOperation(Type operandType, BinaryOperation.Type operation) {
        return switch (operation) {
            case ADD, SUB, MULT, DIV, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL -> operandType.isNumberType();
            case MOD, BITWISE_AND, BITWISE_OR, BITWISE_XOR -> operandType.isIntegerType();
            case LOGICAL_AND, LOGICAL_OR -> operandType == Type.BOOL;
            case EQUAL, NOT_EQUAL -> operandType != Type.VOID;
        };
    }

    private Type analyzeExpression(Expression expression, Scope scope) {
        return switch (expression) {
            case FunctionCall         functionCall         -> analyzeFunctionCall(functionCall, scope);
            case BinaryOperation      binaryOperation      -> analyzeBinaryOperation(binaryOperation, scope);
            case UnaryOperation       unaryOperation       -> analyzeUnaryOperation(unaryOperation, scope);
            case VariableExpression   variableExpression   -> analyzeVariableExpression(variableExpression, scope);
            case FloatingPointLiteral floatingPointLiteral -> analyzeFloatingPointLiteral(floatingPointLiteral, scope);
            case IntegerLiteral       integerLiteral       -> analyzeIntegerLiteral(integerLiteral, scope);
            case BooleanLiteral       booleanLiteral       -> Type.BOOL;
        };
    }

    private Type analyzeVariableExpression(VariableExpression variableExpression, Scope scope) {
        if (!scope.hasVariable(variableExpression.name())) {
            errors.add(new PermissibleError("Variable with name '" + variableExpression.name() + "' is not defined in scope"));
            return Type.VOID;
        }

        VariableSymbol target = scope.getVariable(variableExpression.name());
        return target.type();
    }

    private Type analyzeUnaryOperation(UnaryOperation unaryOperation, Scope scope) {
        var operation = unaryOperation.operation();
        var operandType = analyzeExpression(unaryOperation.operand(), scope);
        if (!isValidUnaryOperation(operandType, operation)) {
            errors.add(new PermissibleError("Operator '" + operation + "' is not valid on type '" + operandType + "'"));
        }
        return operandType;
    }

    private boolean isValidUnaryOperation(Type operandType, UnaryOperation.Type operation) {
        return switch (operation) {
            case NEGATION -> operandType.isSignedNumberType();
            case BITWISE_NOT -> operandType.isIntegerType();
            case LOGICAL_NOT -> operandType == Type.BOOL;
        };
    }

    private Type analyzeIntegerLiteral(IntegerLiteral integerLiteral, Scope scope) {
        return Type.INT;
    }

    private Type analyzeFunctionCall(FunctionCall functionCall, Scope scope) {
        if (!scope.hasFunction(functionCall.name())) {
            errors.add(new PermissibleError("Function with name '" + functionCall.name() + "' is not defined in scope"));
            return Type.VOID;
        }

        FunctionSymbol target = scope.getFunction(functionCall.name());

        List<Type> parameterTypes = target.parameterTypes();
        List<Type> argumentTypes = new ArrayList<>();
        for (Expression argument : functionCall.arguments()) {
            Type argumentType = analyzeExpression(argument, scope);
            argumentTypes.add(argumentType);
        }

        if (!argumentTypes.equals(parameterTypes)) {
            errors.add(new PermissibleError(
                """
                Type mismatch for function '%s'
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

        return target.returnType();
    }

    private Type analyzeFloatingPointLiteral(FloatingPointLiteral floatingPointLiteral, Scope scope) {
        // Assumes from the lexer that the literal starts with a digit, contains one or more digits and one or more '.' characters
        String literalValue = floatingPointLiteral.value();
        if (literalValue.endsWith(".")) {
            errors.add(new PermissibleError("Floating point literal cannot end with a decimal point"));
        }

        int decimalIndex = literalValue.indexOf('.');
        if (literalValue.indexOf('.', decimalIndex + 1) >= 0) {
            errors.add(new PermissibleError("Floating point literal cannot contain more than one decimal point"));
        }

        return Type.DOUBLE;
    }

    private Type analyzeBinaryOperation(BinaryOperation binaryOperation, Scope scope) {
        Type leftType = analyzeExpression(binaryOperation.left(), scope);
        Type rightType = analyzeExpression(binaryOperation.right(), scope);
        if (leftType != rightType) {
            errors.add(new PermissibleError("Types differ for binary operation. Left type: " + leftType + ". Right type: " + rightType));
        }

        Type operandType = leftType; // Assume left operand type for further analysis
        BinaryOperation.Type operation = binaryOperation.operation();
        if (!isValidBinaryOperation(operandType, operation)) {
            errors.add(new PermissibleError("Operator '" + operation + "' is not valid between values of type '" + operandType + "'"));
        }

        return getBinaryOperationResultType(operandType, operation);
    }

    private Type getBinaryOperationResultType(Type operandType, BinaryOperation.Type operation) {
        return operation.isComparisonOperation() ? Type.BOOL : operandType;
    }

    private StatementAnalysisResult analyzeAssignment(Assignment assignment, Scope scope) {
        if (!scope.hasVariable(assignment.variableName())) {
            errors.add(new PermissibleError("Could not find variable with name '" + assignment.variableName() + "'"));
            analyzeExpression(assignment.value(), scope);
            return new StatementAnalysisResult(false);
        }

        VariableSymbol target = scope.getVariable(assignment.variableName());

        Type assignmentType = analyzeExpression(assignment.value(), scope);

        if (target.type() != assignmentType) {
            errors.add(new PermissibleError("Variable '" + target.name() + "' with type '" + target.type() + "' assigned to value of type '" + assignmentType + "'"));
        }

        return new StatementAnalysisResult(false);
    }

    private StatementAnalysisResult analyzeVariableDeclaration(VariableDeclaration variableDeclaration, Scope scope) {
        if (scope.hasVariable(variableDeclaration.name())) {
            errors.add(new PermissibleError("Variable with name '" + variableDeclaration.name() + "' conflicts with another variable in scope"));
            return new StatementAnalysisResult(false);
        }

        if (variableDeclaration.type() == Type.VOID) {
            errors.add(new PermissibleError("Variable cannot be declared as void type"));
        }

        scope.addVariable(variableDeclaration);

        if (variableDeclaration.initialValue().isPresent()) {
            Type initialValueType = analyzeExpression(variableDeclaration.initialValue().get(), scope);
            if (variableDeclaration.type() != initialValueType) {
                errors.add(new PermissibleError("Variable '" + variableDeclaration.name() + "' with type '" + variableDeclaration.type() + "' assigned to value of type '" + initialValueType + "'"));
            }
        }

        return new StatementAnalysisResult(false);
    }

    private StatementAnalysisResult analyzeReturnStatement(ReturnStatement returnStatement, Scope scope, Type functionReturnType) {
        if (returnStatement.value().isPresent()) {
            if (functionReturnType == Type.VOID) {
                errors.add(new PermissibleError("Returned a value in void function"));
                return new StatementAnalysisResult(true);
            }
            Type returnType = analyzeExpression(returnStatement.value().get(), scope);
            if (returnType != functionReturnType) {
                errors.add(new PermissibleError("Returned value of type '" + returnType + "' in function with return type of '" + functionReturnType + "'"));
            }
        } else if (functionReturnType != Type.VOID) {
            errors.add(new PermissibleError("Return value expected"));
        }

        return new StatementAnalysisResult(true);
    }

    private StatementAnalysisResult analyzeForStatement(ForStatement forStatement, Scope scope, Type functionReturnType) {
        final Scope headerScope = new Scope(scope);
        if (forStatement.initializer().isPresent()) {
            switch (forStatement.initializer().get()) {
                case VariableDeclaration initVariable -> analyzeVariableDeclaration(initVariable, headerScope);
                case Assignment          assignment   -> analyzeAssignment(assignment, headerScope);
            }
        }

        var alwaysTrue = false;

        if (forStatement.condition().isPresent()) {
            Type conditionType = analyzeExpression(forStatement.condition().get(), headerScope);
            if (conditionType != Type.BOOL) {
                errors.add(new PermissibleError("For loop contains non-boolean condition"));
            }
            alwaysTrue = forStatement.condition().get() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;
        }

        if (forStatement.update().isPresent()) {
            analyzeAssignment(forStatement.update().get(), headerScope);
        }

        var analysisResult = analyzeStatement(forStatement.body(), headerScope, functionReturnType);

        return new StatementAnalysisResult(alwaysTrue && analysisResult.hasGuaranteedReturn());
    }

    private StatementAnalysisResult analyzeDoWhileStatement(DoWhileStatement doWhileStatement, Scope scope, Type functionReturnType) {
        var analysisResult = analyzeStatement(doWhileStatement.body(), scope, functionReturnType);

        Type conditionType = analyzeExpression(doWhileStatement.condition(), scope);
        if (conditionType != Type.BOOL) {
            errors.add(new PermissibleError("Do-While loop contains non-boolean condition"));
        }

        return new StatementAnalysisResult(analysisResult.hasGuaranteedReturn());
    }


    private StatementAnalysisResult analyzeWhileStatement(WhileStatement whileStatement, Scope scope, Type functionReturnType) {
        Type conditionType = analyzeExpression(whileStatement.condition(), scope);
        if (conditionType != Type.BOOL) {
            errors.add(new PermissibleError("While loop contains non-boolean condition"));
        }

        var alwaysTrue = whileStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var analysisResult = analyzeStatement(whileStatement.body(), scope, functionReturnType);

        var hasGuaranteedReturn = alwaysTrue && analysisResult.hasGuaranteedReturn();
        return new StatementAnalysisResult(hasGuaranteedReturn);
    }

    private StatementAnalysisResult analyzeIfStatement(IfStatement ifStatement, Scope scope, Type functionReturnType) {
        Type conditionType = analyzeExpression(ifStatement.condition(), scope);
        if (conditionType != Type.BOOL) {
            errors.add(new PermissibleError("If statement contains non-boolean condition"));
        }

        var alwaysTrue = ifStatement.condition() instanceof BooleanLiteral b && b.value() == BooleanLiteral.Value.TRUE;

        var bodyAnalysisResult = analyzeStatement(ifStatement.body(), scope, functionReturnType);

        if (ifStatement.elseBody().isPresent()) {
            var elseBodyAnalysisResult = analyzeStatement(ifStatement.elseBody().get(), scope, functionReturnType);
            var hasGuaranteedReturn = bodyAnalysisResult.hasGuaranteedReturn() && (alwaysTrue || elseBodyAnalysisResult.hasGuaranteedReturn());
            return new StatementAnalysisResult(hasGuaranteedReturn);
        }

        return new StatementAnalysisResult(alwaysTrue && bodyAnalysisResult.hasGuaranteedReturn());
    }

}
