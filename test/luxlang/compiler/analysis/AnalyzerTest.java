package luxlang.compiler.analysis;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.IntegerLiteral;
import luxlang.compiler.parser.nodes.statements.CodeBlock;
import luxlang.compiler.parser.nodes.statements.ReturnStatement;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;
import luxlang.compiler.parser.objects.SourceInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {

    private SourceInfo dummySourceInfo() {
        Token dummyToken = new Token(TokenKind.EOF, "", 1, 1);
        return new SourceInfo(dummyToken, dummyToken);
    }

    @Test
    public void simple_function() {
        // int main() { return 0; }
        IntegerLiteral zero = new IntegerLiteral("0", dummySourceInfo());
        ReturnStatement returnStmt = new ReturnStatement(Optional.of(zero), dummySourceInfo());
        CodeBlock body = new CodeBlock(List.of(returnStmt), dummySourceInfo());
        
        FunctionDeclaration function = new FunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            dummySourceInfo()
        );
        
        Program program = new Program(List.of(function));
        
        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        
        assertInstanceOf(AnalysisResult.Success.class, result);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) result).analyzedProgram();
        
        assertEquals(1, analyzedProgram.functionDeclarations().size());
        AnalyzedFunctionDeclaration analyzedFunction = analyzedProgram.functionDeclarations().get(0);
        assertEquals("main", analyzedFunction.name());
        assertEquals(Type.INT, analyzedFunction.returnType());
        assertEquals(0, analyzedFunction.parameters().size());
    }

    @Test
    public void function_with_parameters() {
        // int add(int a, int b) { return 0; }
        IntegerLiteral zero = new IntegerLiteral("0", dummySourceInfo());
        ReturnStatement returnStmt = new ReturnStatement(Optional.of(zero), dummySourceInfo());
        CodeBlock body = new CodeBlock(List.of(returnStmt), dummySourceInfo());
        
        Parameter param1 = new Parameter(Type.INT, "a", dummySourceInfo());
        Parameter param2 = new Parameter(Type.INT, "b", dummySourceInfo());
        
        FunctionDeclaration function = new FunctionDeclaration(
            Type.INT,
            "add",
            List.of(param1, param2),
            body,
            dummySourceInfo()
        );
        
        Program program = new Program(List.of(function));
        
        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        
        assertInstanceOf(AnalysisResult.Success.class, result);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) result).analyzedProgram();
        
        AnalyzedFunctionDeclaration analyzedFunction = analyzedProgram.functionDeclarations().get(0);
        assertEquals("add", analyzedFunction.name());
        assertEquals(2, analyzedFunction.parameters().size());
        assertEquals(2, analyzedFunction.localVariables().size());
        
        // Parameters should be in local variables
        List<LocalVariable> locals = analyzedFunction.localVariables();
        assertTrue(locals.stream().anyMatch(v -> v.name().equals("a")));
        assertTrue(locals.stream().anyMatch(v -> v.name().equals("b")));
    }

    @Test
    public void local_variable_tracking() {
        // int main() { int x = 10; int y = 20; return 0; }
        IntegerLiteral ten = new IntegerLiteral("10", dummySourceInfo());
        IntegerLiteral twenty = new IntegerLiteral("20", dummySourceInfo());
        IntegerLiteral zero = new IntegerLiteral("0", dummySourceInfo());
        
        VariableDeclaration varX = new VariableDeclaration(
            Type.INT, "x", Optional.of(ten), dummySourceInfo()
        );
        VariableDeclaration varY = new VariableDeclaration(
            Type.INT, "y", Optional.of(twenty), dummySourceInfo()
        );
        ReturnStatement returnStmt = new ReturnStatement(Optional.of(zero), dummySourceInfo());
        
        CodeBlock body = new CodeBlock(List.of(varX, varY, returnStmt), dummySourceInfo());
        
        FunctionDeclaration function = new FunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            dummySourceInfo()
        );
        
        Program program = new Program(List.of(function));
        
        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        
        assertInstanceOf(AnalysisResult.Success.class, result);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) result).analyzedProgram();
        
        AnalyzedFunctionDeclaration analyzedFunction = analyzedProgram.functionDeclarations().get(0);
        List<LocalVariable> locals = analyzedFunction.localVariables();
        
        assertEquals(2, locals.size());
        
        // Check that variables have unique IDs
        long uniqueIds = locals.stream().map(LocalVariable::id).distinct().count();
        assertEquals(locals.size(), uniqueIds);
        
        // Check variable names
        assertTrue(locals.stream().anyMatch(v -> v.name().equals("x")));
        assertTrue(locals.stream().anyMatch(v -> v.name().equals("y")));
    }

    @Test
    public void multiple_functions() {
        // int foo() { return 1; } int bar() { return 2; }
        IntegerLiteral one = new IntegerLiteral("1", dummySourceInfo());
        IntegerLiteral two = new IntegerLiteral("2", dummySourceInfo());
        
        ReturnStatement returnOne = new ReturnStatement(Optional.of(one), dummySourceInfo());
        ReturnStatement returnTwo = new ReturnStatement(Optional.of(two), dummySourceInfo());
        
        CodeBlock body1 = new CodeBlock(List.of(returnOne), dummySourceInfo());
        CodeBlock body2 = new CodeBlock(List.of(returnTwo), dummySourceInfo());
        
        FunctionDeclaration func1 = new FunctionDeclaration(
            Type.INT, "foo", List.of(), body1, dummySourceInfo()
        );
        FunctionDeclaration func2 = new FunctionDeclaration(
            Type.INT, "bar", List.of(), body2, dummySourceInfo()
        );
        
        Program program = new Program(List.of(func1, func2));
        
        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        
        assertInstanceOf(AnalysisResult.Success.class, result);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) result).analyzedProgram();
        
        assertEquals(2, analyzedProgram.functionDeclarations().size());
        assertEquals("foo", analyzedProgram.functionDeclarations().get(0).name());
        assertEquals("bar", analyzedProgram.functionDeclarations().get(1).name());
    }

    @Test
    public void void_function() {
        // void empty() { return; }
        ReturnStatement returnStmt = new ReturnStatement(Optional.empty(), dummySourceInfo());
        CodeBlock body = new CodeBlock(List.of(returnStmt), dummySourceInfo());
        
        FunctionDeclaration function = new FunctionDeclaration(
            Type.VOID,
            "empty",
            List.of(),
            body,
            dummySourceInfo()
        );
        
        Program program = new Program(List.of(function));
        
        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        
        assertInstanceOf(AnalysisResult.Success.class, result);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) result).analyzedProgram();
        
        AnalyzedFunctionDeclaration analyzedFunction = analyzedProgram.functionDeclarations().get(0);
        assertEquals("empty", analyzedFunction.name());
        assertEquals(Type.VOID, analyzedFunction.returnType());
        assertEquals(0, analyzedFunction.localVariables().size());
    }
}
