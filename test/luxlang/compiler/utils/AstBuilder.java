package luxlang.compiler.utils;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.analysis.nodes.expressions.AnalyzedFunctionCall;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.*;
import luxlang.compiler.parser.nodes.statements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AstBuilder {

    public static IntegerLiteral intLiteral(String value) {
        return new IntegerLiteral(value, TestUtils.dummySourceInfo());
    }

    public static BinaryOperation binaryOp(
            BinaryOperation.BinaryOperationType operation,
            Expression left, Expression right) {
        return new BinaryOperation(operation, left, right, TestUtils.dummySourceInfo());
    }
    
    public static UnaryOperation unaryOp(
            UnaryOperation.UnaryOperationType operation,
            Expression operand) {
        return new UnaryOperation(operation, operand, TestUtils.dummySourceInfo());
    }
    
    public static VariableExpression varExpr(String name) {
        return new VariableExpression(name, TestUtils.dummySourceInfo());
    }
    
    public static BooleanLiteral boolLiteral(boolean value) {
        BooleanLiteral.Value enumValue =
            value ? BooleanLiteral.Value.TRUE
                  : BooleanLiteral.Value.FALSE;
        return new BooleanLiteral(enumValue, TestUtils.dummySourceInfo());
    }

    public static ReturnStatement returnStmt(Expression value) {
        return new ReturnStatement(Optional.of(value), TestUtils.dummySourceInfo());
    }
    
    public static ReturnStatement returnStmt() {
        return new ReturnStatement(Optional.empty(), TestUtils.dummySourceInfo());
    }
    
    public static VariableDeclaration varDecl(Type type, String name, Expression initializer) {
        return new VariableDeclaration(type, name, Optional.of(initializer), TestUtils.dummySourceInfo());
    }
    
    public static VariableDeclaration varDecl(Type type, String name) {
        return new VariableDeclaration(type, name, Optional.empty(), TestUtils.dummySourceInfo());
    }

    public static FunctionCall funcCall(String name, Expression... arguments) {
        return new FunctionCall(name, List.of(arguments), TestUtils.dummySourceInfo());
    }

    public static Assignment assignment(String left, Expression right) {
        return new Assignment(left, right, TestUtils.dummySourceInfo());
    }
    
    public static CodeBlock codeBlock(Statement... statements) {
        return new CodeBlock(List.of(statements), TestUtils.dummySourceInfo());
    }
    
    public static IfStatement ifStmt(Expression condition, Statement body) {
        return new IfStatement(condition, body, Optional.empty(), TestUtils.dummySourceInfo());
    }
    
    public static IfStatement ifStmt(Expression condition, Statement body, Statement elseBody) {
        return new IfStatement(condition, body, Optional.of(elseBody), TestUtils.dummySourceInfo());
    }
    
    public static WhileStatement whileStmt(Expression condition, Statement body) {
        return new WhileStatement(condition, body, TestUtils.dummySourceInfo());
    }

    public static DoWhileStatement doWhileStmt(Statement body, Expression condition) {
        return new DoWhileStatement(body, condition, TestUtils.dummySourceInfo());
    }

    public static ForStatement forStmt(
            ForStatement.Initializer initializer,
            Expression condition,
            Assignment update,
            Statement body) {
        return new ForStatement(
            Optional.ofNullable(initializer),
            Optional.ofNullable(condition),
            Optional.ofNullable(update),
            body,
            TestUtils.dummySourceInfo()
        );
    }

    public static FloatingPointLiteral floatLiteral(String value) {
        return new FloatingPointLiteral(value, TestUtils.dummySourceInfo());
    }

    public static Parameter param(Type type, String name) {
        return new Parameter(type, name, TestUtils.dummySourceInfo());
    }

    public static class FunctionBuilder {
        private Type returnType = Type.VOID;
        private String name = "function";
        private List<Parameter> parameters = new ArrayList<>();
        private List<Statement> statements = new ArrayList<>();
        
        public FunctionBuilder returnType(Type type) {
            this.returnType = type;
            return this;
        }
        
        public FunctionBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public FunctionBuilder param(Type type, String name) {
            this.parameters.add(AstBuilder.param(type, name));
            return this;
        }
        
        public FunctionBuilder statement(Statement stmt) {
            this.statements.add(stmt);
            return this;
        }

        public FunctionDeclaration build() {
            CodeBlock body = new CodeBlock(statements, TestUtils.dummySourceInfo());
            return new FunctionDeclaration(returnType, name, parameters, body, TestUtils.dummySourceInfo());
        }
    }
    
    public static FunctionBuilder functionBuilder() {
        return new FunctionBuilder();
    }

    public static Program program(FunctionDeclaration... functions) {
        return new Program(List.of(functions));
    }
}
