package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.analysis.nodes.expressions.AnalyzedIntegerLiteral;
import luxlang.compiler.analysis.nodes.statements.AnalyzedCodeBlock;
import luxlang.compiler.analysis.nodes.statements.AnalyzedReturnStatement;
import luxlang.compiler.analysis.nodes.statements.AnalyzedStatement;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for building analyzed AST nodes in IR builder tests.
 * Provides factory methods and builders for creating test data structures.
 */
public class IRBuilderTestUtils {
    
    // Expression builders
    
    public static AnalyzedIntegerLiteral intLiteral(long value, Type type) {
        return new AnalyzedIntegerLiteral(value, type, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedIntegerLiteral intLiteral(int value) {
        return intLiteral(value, Type.INT);
    }
    
    // Statement builders
    
    public static AnalyzedReturnStatement returnStmt(AnalyzedExpression value) {
        return new AnalyzedReturnStatement(Optional.of(value), TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedReturnStatement returnStmt() {
        return new AnalyzedReturnStatement(Optional.empty(), TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedCodeBlock codeBlock(boolean hasGuaranteedReturn, AnalyzedStatement... statements) {
        return new AnalyzedCodeBlock(List.of(statements), hasGuaranteedReturn, TestUtils.dummySourceInfo());
    }
    
    public static AnalyzedCodeBlock codeBlock(AnalyzedStatement... statements) {
        return codeBlock(true, statements);
    }
    
    // Local variable builders
    
    public static LocalVariable localVar(int id, String name, Type type) {
        return new LocalVariable(id, name, type);
    }
    
    // Function builders
    
    public static class AnalyzedFunctionBuilder {
        private Type returnType = Type.VOID;
        private String name = "function";
        private List<Parameter> parameters = new ArrayList<>();
        private List<LocalVariable> localVariables = new ArrayList<>();
        private List<AnalyzedStatement> statements = new ArrayList<>();
        private boolean hasGuaranteedReturn = true;
        
        public AnalyzedFunctionBuilder returnType(Type type) {
            this.returnType = type;
            return this;
        }
        
        public AnalyzedFunctionBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public AnalyzedFunctionBuilder param(Type type, String name) {
            this.parameters.add(new Parameter(type, name, TestUtils.dummySourceInfo()));
            return this;
        }
        
        public AnalyzedFunctionBuilder localVar(int id, String name, Type type) {
            this.localVariables.add(new LocalVariable(id, name, type));
            return this;
        }
        
        public AnalyzedFunctionBuilder statement(AnalyzedStatement stmt) {
            this.statements.add(stmt);
            return this;
        }
        
        public AnalyzedFunctionBuilder returnValue(AnalyzedExpression expr) {
            this.statements.add(returnStmt(expr));
            return this;
        }
        
        public AnalyzedFunctionBuilder returnVoid() {
            this.statements.add(returnStmt());
            return this;
        }
        
        public AnalyzedFunctionBuilder guaranteedReturn(boolean value) {
            this.hasGuaranteedReturn = value;
            return this;
        }
        
        public AnalyzedFunctionDeclaration build() {
            AnalyzedCodeBlock body = new AnalyzedCodeBlock(statements, hasGuaranteedReturn, TestUtils.dummySourceInfo());
            return new AnalyzedFunctionDeclaration(
                returnType, 
                name, 
                parameters, 
                body, 
                localVariables, 
                TestUtils.dummySourceInfo()
            );
        }
    }
    
    public static AnalyzedFunctionBuilder function() {
        return new AnalyzedFunctionBuilder();
    }
    
    // Program builders
    
    public static AnalyzedProgram program(AnalyzedFunctionDeclaration... functions) {
        return new AnalyzedProgram(List.of(functions));
    }
}
