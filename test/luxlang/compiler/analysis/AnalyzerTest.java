package luxlang.compiler.analysis;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.parser.nodes.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static luxlang.compiler.analysis.AnalyzerTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {

    @Test
    public void simple_function() {
        // int main() { return 0; }
        Program program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .returnValue(intLiteral(0))
                .build()
        );
        
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
        Program program = program(
            function()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .returnValue(intLiteral(0))
                .build()
        );
        
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
        Program program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "x", intLiteral(10)))
                .statement(varDecl(Type.INT, "y", intLiteral(20)))
                .returnValue(intLiteral(0))
                .build()
        );
        
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
        Program program = program(
            function()
                .returnType(Type.INT)
                .name("foo")
                .returnValue(intLiteral(1))
                .build(),
            function()
                .returnType(Type.INT)
                .name("bar")
                .returnValue(intLiteral(2))
                .build()
        );
        
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
        Program program = program(
            function()
                .returnType(Type.VOID)
                .name("empty")
                .returnVoid()
                .build()
        );
        
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
