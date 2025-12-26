package luxlang.compiler.analysis;

import luxlang.compiler.analysis.errors.*;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.parser.nodes.expressions.UnaryOperation.UnaryOperationType;
import org.junit.jupiter.api.Test;

import static luxlang.compiler.utils.AnalyzedAstBuilder.*;
import static luxlang.compiler.utils.AstBuilder.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

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

    @Test
    public void error_duplicate_function_name() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("foo")
                .build(),
            functionBuilder()
                .returnType(Type.VOID)
                .name("foo")
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(DuplicateFunctionNameError.class));
    }

    @Test
    public void error_duplicate_variable_name() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(varDecl(Type.INT, "x", intLiteral("1")))
                .statement(varDecl(Type.INT, "x", intLiteral("2")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(DuplicateVariableNameError.class));
    }

    @Test
    public void error_duplicate_parameter_name() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .param(Type.INT, "a")
                .param(Type.INT, "a")
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(DuplicateVariableNameError.class));
    }

    @Test
    public void error_void_variable() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(varDecl(Type.VOID, "x"))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(VoidVariableError.class));
    }

    @Test
    public void error_void_parameter() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .param(Type.VOID, "x")
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(VoidVariableError.class));
    }

    @Test
    public void error_undefined_variable_in_expression() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(varExpr("x")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(UndefinedVariableError.class));
    }

    @Test
    public void error_undefined_variable_in_assignment() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(assignment("x", intLiteral("1")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(UndefinedVariableError.class));
    }

    @Test
    public void error_undefined_function() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(funcCall("foo")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(UndefinedFunctionError.class));
    }

    @Test
    public void error_type_mismatch_in_assignment() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(varDecl(Type.INT, "x", intLiteral("1")))
                .statement(assignment("x", boolLiteral(true)))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(TypeMismatchError.class));
    }

    @Test
    public void error_type_mismatch_in_variable_declaration() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(varDecl(Type.INT, "x", boolLiteral(true)))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(TypeMismatchError.class));
    }

    @Test
    public void error_indeterminate_return() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .param(Type.BOOL, "condition")
                .statement(ifStmt(
                    varExpr("condition"),
                    codeBlock(returnStmt(intLiteral("1")))
                ))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(IndeterminateReturnError.class));
    }

    @Test
    public void error_return_value_in_void_function() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(returnStmt(intLiteral("1")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(ReturnTypeError.class));
    }

    @Test
    public void error_return_type_mismatch() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(boolLiteral(true)))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(ReturnTypeError.class));
    }

    @Test
    public void error_return_missing_value() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt())
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(ReturnMissingValueError.class));
    }

    @Test
    public void error_invalid_condition_in_if() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(ifStmt(intLiteral("1"), codeBlock()))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(InvalidConditionError.class));
    }

    @Test
    public void error_invalid_condition_in_while() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(whileStmt(intLiteral("1"), codeBlock()))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(InvalidConditionError.class));
    }

    @Test
    public void error_invalid_condition_in_do_while() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(doWhileStmt(codeBlock(), intLiteral("1")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(InvalidConditionError.class));
    }

    @Test
    public void error_invalid_condition_in_for() {
        Program input = program(
            functionBuilder()
                .returnType(Type.VOID)
                .name("main")
                .statement(forStmt(null, intLiteral("1"), null, codeBlock()))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(InvalidConditionError.class));
    }

    @Test
    public void error_unreachable_statement() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(intLiteral("1")))
                .statement(returnStmt(intLiteral("2")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(UnreachableStatementError.class));
    }

    @Test
    public void error_invalid_unary_operation() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(unaryOp(UnaryOperationType.LOGICAL_NOT, intLiteral("1"))))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(InvalidOperationError.class));
    }

    @Test
    public void error_argument_type_mismatch() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("foo")
                .param(Type.INT, "x")
                .statement(returnStmt(varExpr("x")))
                .build(),
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(funcCall("foo", boolLiteral(true))))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(ArgumentTypeMismatchError.class));
    }

    @Test
    public void error_integer_literal_overflow() {
        Program input = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(intLiteral("99999999999999999999")))
                .build()
        );

        Analyzer analyzer = new Analyzer(input);
        AnalysisResult result = analyzer.analyze();

        assertThat(result).isInstanceOf(AnalysisResult.Failure.class);
        var errors = ((AnalysisResult.Failure) result).errors();
        assertThat(errors).singleElement(type(LiteralOverflowError.class));
    }
}
