package luxlang.compiler.analysis;

import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.Expression;
import luxlang.compiler.parser.nodes.expressions.IntegerLiteral;
import luxlang.compiler.parser.nodes.statements.CodeBlock;
import luxlang.compiler.parser.nodes.statements.ReturnStatement;
import luxlang.compiler.parser.nodes.statements.Statement;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;
import luxlang.compiler.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for building parser AST nodes in analyzer tests.
 * Provides factory methods and builders for creating test data structures.
 */
public class AnalyzerTestUtils {
    
    // Expression builders
    
    public static IntegerLiteral intLiteral(String value) {
        return new IntegerLiteral(value, TestUtils.dummySourceInfo());
    }
    
    public static IntegerLiteral intLiteral(int value) {
        return intLiteral(String.valueOf(value));
    }
    
    // Statement builders
    
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
    
    public static CodeBlock codeBlock(Statement... statements) {
        return new CodeBlock(List.of(statements), TestUtils.dummySourceInfo());
    }
    
    // Parameter builders
    
    public static Parameter param(Type type, String name) {
        return new Parameter(type, name, TestUtils.dummySourceInfo());
    }
    
    // Function builders
    
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
            this.parameters.add(AnalyzerTestUtils.param(type, name));
            return this;
        }
        
        public FunctionBuilder statement(Statement stmt) {
            this.statements.add(stmt);
            return this;
        }
        
        public FunctionBuilder returnValue(Expression expr) {
            this.statements.add(returnStmt(expr));
            return this;
        }
        
        public FunctionBuilder returnVoid() {
            this.statements.add(returnStmt());
            return this;
        }
        
        public FunctionDeclaration build() {
            CodeBlock body = new CodeBlock(statements, TestUtils.dummySourceInfo());
            return new FunctionDeclaration(returnType, name, parameters, body, TestUtils.dummySourceInfo());
        }
    }
    
    public static FunctionBuilder function() {
        return new FunctionBuilder();
    }
    
    // Program builders
    
    public static Program program(FunctionDeclaration... functions) {
        return new Program(List.of(functions));
    }
}
