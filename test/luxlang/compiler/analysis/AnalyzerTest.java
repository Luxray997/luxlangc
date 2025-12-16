package luxlang.compiler.analysis;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.*;
import org.junit.jupiter.api.Test;

import static luxlang.compiler.analysis.AnalyzerTestUtils.*;
import static org.assertj.core.api.Assertions.*;

public class AnalyzerTest {

    @Test
    public void simple_function() {
        // int main() { return 0; }
        Program input = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .returnValue(intLiteral(0))
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult actual = analyzer.analyze();
        
        assertThat(actual).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actualProgram = ((AnalysisResult.Success) actual).analyzedProgram();
        
        assertThat(actualProgram.functionDeclarations())
            .hasSize(1)
            .first()
            .satisfies(function -> {
                assertThat(function.name()).isEqualTo("main");
                assertThat(function.returnType()).isEqualTo(Type.INT);
                assertThat(function.parameters()).isEmpty();
            });
    }

    @Test
    public void function_with_parameters() {
        // int add(int a, int b) { return 0; }
        Program input = program(
            function()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .returnValue(intLiteral(0))
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult actual = analyzer.analyze();
        
        assertThat(actual).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actualProgram = ((AnalysisResult.Success) actual).analyzedProgram();
        
        assertThat(actualProgram.functionDeclarations().get(0))
            .satisfies(function -> {
                assertThat(function.name()).isEqualTo("add");
                assertThat(function.parameters()).hasSize(2);
                assertThat(function.localVariables())
                    .hasSize(2)
                    .extracting("name")
                    .containsExactlyInAnyOrder("a", "b");
            });
    }

    @Test
    public void local_variable_tracking() {
        // int main() { int x = 10; int y = 20; return 0; }
        Program input = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "x", intLiteral(10)))
                .statement(varDecl(Type.INT, "y", intLiteral(20)))
                .returnValue(intLiteral(0))
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult actual = analyzer.analyze();
        
        assertThat(actual).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actualProgram = ((AnalysisResult.Success) actual).analyzedProgram();
        
        assertThat(actualProgram.functionDeclarations().get(0).localVariables())
            .hasSize(2)
            .extracting("name")
            .containsExactlyInAnyOrder("x", "y");
        
        // Check that variables have unique IDs
        assertThat(actualProgram.functionDeclarations().get(0).localVariables())
            .extracting("id")
            .doesNotHaveDuplicates();
    }

    @Test
    public void multiple_functions() {
        // int foo() { return 1; } int bar() { return 2; }
        Program input = program(
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
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult actual = analyzer.analyze();
        
        assertThat(actual).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actualProgram = ((AnalysisResult.Success) actual).analyzedProgram();
        
        assertThat(actualProgram.functionDeclarations())
            .hasSize(2)
            .extracting("name")
            .containsExactly("foo", "bar");
    }

    @Test
    public void void_function() {
        // void empty() { return; }
        Program input = program(
            function()
                .returnType(Type.VOID)
                .name("empty")
                .returnVoid()
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult actual = analyzer.analyze();
        
        assertThat(actual).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actualProgram = ((AnalysisResult.Success) actual).analyzedProgram();
        
        assertThat(actualProgram.functionDeclarations().get(0))
            .satisfies(function -> {
                assertThat(function.name()).isEqualTo("empty");
                assertThat(function.returnType()).isEqualTo(Type.VOID);
                assertThat(function.localVariables()).isEmpty();
            });
    }
}
