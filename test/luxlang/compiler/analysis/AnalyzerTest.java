package luxlang.compiler.analysis;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import org.junit.jupiter.api.Test;

import static luxlang.compiler.utils.AnalyzedAstBuilder.*;
import static luxlang.compiler.utils.AstBuilder.*;
import static org.assertj.core.api.Assertions.*;

public class AnalyzerTest {

    @Test
    public void simple_function() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(intLiteral("0")))
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedReturnStmt(analyzedIntLiteral(0, Type.INT)))
                .hasGuaranteedReturn(true)
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();
        
        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void function_with_parameters() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .statement(returnStmt(binaryOp(
                    BinaryOperationType.ADD,
                    varExpr("a"),
                    varExpr("b")
                )))
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .statement(analyzedReturnStmt(analyzedBinaryOp(
                        BinaryOperationType.ADD,
                        analyzedVarExpr("a", Type.INT),
                        analyzedVarExpr("b", Type.INT),
                        Type.INT
                )))
                .hasGuaranteedReturn(true)
                .localVar(0, "a", Type.INT)
                .localVar(1, "b", Type.INT)
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();
        
        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void local_variable_tracking() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "x", intLiteral("10")))
                .statement(varDecl(Type.INT, "y", intLiteral("20")))
                .statement(returnStmt(intLiteral("0")))
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(10, Type.INT)))
                .statement(analyzedVarDecl(Type.INT, "y", analyzedIntLiteral(20, Type.INT)))
                .statement(analyzedReturnStmt(analyzedIntLiteral(0, Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "x", Type.INT)
                .localVar(1, "y", Type.INT)
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();
        
        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void multiple_functions() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("foo")
                .statement(returnStmt(intLiteral("1")))
                .build(),
            functionBuilder()
                .returnType(Type.INT)
                .name("bar")
                .statement(returnStmt(intLiteral("2")))
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("foo")
                .statement(analyzedReturnStmt(analyzedIntLiteral(1, Type.INT)))
                .hasGuaranteedReturn(true)
                .build(),
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("bar")
                .statement(analyzedReturnStmt(analyzedIntLiteral(2, Type.INT)))
                .hasGuaranteedReturn(true)
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();
        
        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void empty_function_explicit_return() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .statement(returnStmt())
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .statement(analyzedReturnStmt())
                .hasGuaranteedReturn(true)
                .build()
        );
        
        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();
        
        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void empty_function_implicit_return() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .build()
        );

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .hasGuaranteedReturn(false)
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Success.class);
        AnalyzedProgram actual = ((AnalysisResult.Success) result).analyzedProgram();

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }
}
