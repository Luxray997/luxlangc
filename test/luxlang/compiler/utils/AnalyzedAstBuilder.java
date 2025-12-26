package luxlang.compiler.utils;


import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.analysis.nodes.expressions.*;
import luxlang.compiler.analysis.nodes.statements.*;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.parser.nodes.expressions.UnaryOperation.UnaryOperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnalyzedAstBuilder {

    public static AnalyzedIntegerLiteral analyzedIntLiteral(long value, Type type) {
        return new AnalyzedIntegerLiteral(value, type, TestUtils.dummySourceInfo());
    }

    public static AnalyzedFloatingPointLiteral analyzedFPLiteral(double value, Type type) {
        return new AnalyzedFloatingPointLiteral(value, type, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedBinaryOperation analyzedBinaryOp(
        BinaryOperationType operation,
        AnalyzedExpression left,
        AnalyzedExpression right,
        Type resultType
    ) {
        return new AnalyzedBinaryOperation(operation, left, right, resultType, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedUnaryOperation analyzedUnaryOp(
        UnaryOperationType operation,
        AnalyzedExpression operand,
        Type resultType
    ) {
        return new AnalyzedUnaryOperation(operation, operand, resultType, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedVariableExpression analyzedVarExpr(String name, Type resultType) {
        return new AnalyzedVariableExpression(name, resultType, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedBooleanLiteral analyzedBoolLiteral(boolean value) {
        return new AnalyzedBooleanLiteral(value, TestUtils.dummySourceInfo());
    }

    public static AnalyzedReturnStatement analyzedReturnStmt(AnalyzedExpression value) {
        return new AnalyzedReturnStatement(Optional.of(value), TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedReturnStatement analyzedReturnStmt() {
        return new AnalyzedReturnStatement(Optional.empty(), TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedVariableDeclaration analyzedVarDecl(Type type, String name, AnalyzedExpression initializer) {
        return new AnalyzedVariableDeclaration(type, name, Optional.of(initializer), TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedVariableDeclaration analyzedVarDecl(Type type, String name) {
        return new AnalyzedVariableDeclaration(type, name, Optional.empty(), TestUtils.dummySourceInfo());
    }

    public static AnalyzedForStatement analyzedForStmt(
        AnalyzedForStatement.Initializer initializer,
        AnalyzedExpression condition,
        AnalyzedAssignment update,
        AnalyzedStatement body,
        boolean hasGuaranteedReturn
    ) {
        return new AnalyzedForStatement(
            Optional.of(initializer),
            Optional.of(condition),
            Optional.of(update),
            body,
            hasGuaranteedReturn,
            TestUtils.dummySourceInfo()
        );
    }

    public static AnalyzedFunctionCall analyzedFuncCall(
        String name,
        Type resultType,
        AnalyzedExpression... arguments
    ) {
        return new AnalyzedFunctionCall(name, List.of(arguments), resultType, TestUtils.dummySourceInfo());
    }

    public static AnalyzedAssignment analyzedAssignment(String left, AnalyzedExpression right) {
        return new AnalyzedAssignment(left, right, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedCodeBlock analyzedCodeBlock(boolean hasGuaranteedReturn, AnalyzedStatement... statements) {
        return new AnalyzedCodeBlock(List.of(statements), hasGuaranteedReturn, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedIfStatement analyzedIfStmt(
        AnalyzedExpression condition,
        AnalyzedStatement body,
        boolean hasGuaranteedReturn
    ) {
        return new AnalyzedIfStatement(condition, body, Optional.empty(), hasGuaranteedReturn, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedIfStatement analyzedIfStmt(
        AnalyzedExpression condition,
        AnalyzedStatement body,
        AnalyzedStatement elseBody,
        boolean hasGuaranteedReturn
    ) {
        return new AnalyzedIfStatement(condition, body, Optional.of(elseBody), hasGuaranteedReturn, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedWhileStatement analyzedWhileStmt(AnalyzedExpression condition, AnalyzedStatement body, boolean hasGuaranteedReturn) {
        return new AnalyzedWhileStatement(condition, body, hasGuaranteedReturn, TestUtils.dummySourceInfo());
    }

    public static Parameter param(Type type, String name) {
        return new Parameter(type, name, TestUtils.dummySourceInfo());
    }

    public static class FunctionBuilder {
        private Type returnType = Type.VOID;
        private String name = "function";
        private List<Parameter> parameters = new ArrayList<>();
        private List<AnalyzedStatement> statements = new ArrayList<>();
        private List<LocalVariable> localVariables = new ArrayList<>();
        private boolean hasGuaranteedReturn = false;

        public FunctionBuilder returnType(Type type) {
            this.returnType = type;
            return this;
        }
        
        public FunctionBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public FunctionBuilder param(Type type, String name) {
            this.parameters.add(AnalyzedAstBuilder.param(type, name));
            return this;
        }
        
        public FunctionBuilder statement(AnalyzedStatement stmt) {
            this.statements.add(stmt);
            return this;
        }

        public FunctionBuilder hasGuaranteedReturn(boolean value) {
            this.hasGuaranteedReturn = value;
            return this;
        }

        public FunctionBuilder localVar(int index, String name, Type type) {
            this.localVariables.add(new LocalVariable(index, name, type));
            return this;
        }
        
        public AnalyzedFunctionDeclaration build() {
            AnalyzedCodeBlock body = new AnalyzedCodeBlock(statements, hasGuaranteedReturn, TestUtils.dummySourceInfo());
            return new AnalyzedFunctionDeclaration(returnType, name, parameters, body, localVariables, TestUtils.dummySourceInfo());
        }
    }
    
    public static FunctionBuilder analyzedFunctionBuilder() {
        return new FunctionBuilder();
    }

    public static AnalyzedProgram analyzedProgram(AnalyzedFunctionDeclaration... functions) {
        return new AnalyzedProgram(List.of(functions));
    }
}
