package luxlang.compiler.integration;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static luxlang.compiler.utils.AnalyzedAstBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AnalyzerIntegrationTest {

    @Test
    public void simple_function() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("simple_function.lux");

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedReturnStmt(analyzedIntLiteral(0, Type.INT)))
                .hasGuaranteedReturn(true)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void function_call() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("function_call.lux");

        AnalyzedProgram expected =  analyzedProgram(
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
                .build(),
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(
                    Type.INT,
                    "result",
                    analyzedFuncCall(
                        "add",
                        Type.INT,
                        analyzedIntLiteral(5, Type.INT),
                        analyzedIntLiteral(10, Type.INT)
                    )
                ))
                .statement(analyzedReturnStmt(analyzedVarExpr("result", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "result", Type.INT)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void variable_scope() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("variable_scope.lux");

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(10, Type.INT)))
                .statement(analyzedCodeBlock(
                    false,
                    analyzedVarDecl(Type.INT, "y", analyzedIntLiteral(20, Type.INT)),
                    analyzedAssignment(
                        "x",
                        analyzedBinaryOp(
                            BinaryOperationType.ADD,
                            analyzedVarExpr("x", Type.INT),
                            analyzedVarExpr("y", Type.INT),
                            Type.INT
                        )
                    )
                ))
                .statement(analyzedReturnStmt(analyzedVarExpr("x", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "x", Type.INT)
                .localVar(1, "y", Type.INT)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void type_checking() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("type_checking.lux");
        
        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(10, Type.INT)))
                .statement(analyzedVarDecl(Type.INT, "y", analyzedIntLiteral(20, Type.INT)))
                .statement(analyzedVarDecl(
                    Type.INT,
                    "z",
                    analyzedBinaryOp(
                        BinaryOperationType.ADD,
                        analyzedVarExpr("x", Type.INT),
                        analyzedVarExpr("y", Type.INT),
                        Type.INT
                    )
                ))
                .statement(analyzedVarDecl(
                    Type.BOOL,
                    "b",
                    analyzedBinaryOp(
                        BinaryOperationType.GREATER,
                        analyzedVarExpr("x", Type.INT),
                        analyzedVarExpr("y", Type.INT),
                        Type.BOOL
                    )
                ))
                .statement(analyzedReturnStmt(analyzedVarExpr("z", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "x", Type.INT)
                .localVar(1, "y", Type.INT)
                .localVar(2, "z", Type.INT)
                .localVar(3, "b", Type.BOOL)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void control_flow() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("control_flow.lux");
        
        AnalyzedProgram expected =  analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(10, Type.INT)))
                .statement(analyzedIfStmt(
                    analyzedBinaryOp(
                        BinaryOperationType.GREATER,
                        analyzedVarExpr("x", Type.INT),
                        analyzedIntLiteral(5, Type.INT),
                        Type.BOOL
                    ),
                    analyzedCodeBlock(false,
                        analyzedAssignment(
                            "x",
                            analyzedBinaryOp(
                                BinaryOperationType.ADD,
                                analyzedVarExpr("x", Type.INT),
                                analyzedIntLiteral(1, Type.INT),
                                Type.INT
                            )
                        )
                    ),
                    analyzedCodeBlock(false,
                        analyzedAssignment(
                            "x",
                            analyzedBinaryOp(
                                BinaryOperationType.SUB,
                                analyzedVarExpr("x", Type.INT),
                                analyzedIntLiteral(1, Type.INT),
                                Type.INT
                            )
                        )
                    ),
                    false
                ))
                .statement(analyzedWhileStmt(
                    analyzedBinaryOp(
                        BinaryOperationType.LESS,
                        analyzedVarExpr("x", Type.INT),
                        analyzedIntLiteral(20, Type.INT),
                        Type.BOOL
                    ),
                    analyzedCodeBlock(
                        false,
                        analyzedAssignment(
                            "x",
                            analyzedBinaryOp(
                                BinaryOperationType.ADD,
                                analyzedVarExpr("x", Type.INT),
                                analyzedIntLiteral(1, Type.INT),
                                Type.INT
                                )
                            )
                        ),
                    false
                ))
                .statement(analyzedReturnStmt(analyzedVarExpr("x", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "x", Type.INT)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void for_loop() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("for_loop.lux");

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(
                    Type.INT,
                    "sum",
                    analyzedIntLiteral(0, Type.INT)
                ))
                .statement(analyzedForStmt(
                    analyzedVarDecl(Type.INT, "i", analyzedIntLiteral(0, Type.INT)),
                    analyzedBinaryOp(
                        BinaryOperationType.LESS,
                        analyzedVarExpr("i", Type.INT),
                        analyzedIntLiteral(10, Type.INT),
                        Type.BOOL
                    ),
                    analyzedAssignment(
                        "i",
                        analyzedBinaryOp(
                            BinaryOperationType.ADD,
                            analyzedVarExpr("i", Type.INT),
                            analyzedIntLiteral(1, Type.INT),
                            Type.INT
                        )
                    ),
                    analyzedCodeBlock(
                        false,
                        analyzedAssignment(
                            "sum",
                            analyzedBinaryOp(
                                BinaryOperationType.ADD,
                                analyzedVarExpr("sum", Type.INT),
                                analyzedVarExpr("i", Type.INT),
                                Type.INT
                            )
                        )
                    ),
                    false
                ))
                .statement(analyzedReturnStmt(analyzedVarExpr("sum", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "sum", Type.INT)
                .localVar(1, "i", Type.INT)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void expressions() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("expressions.lux");

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(
                    Type.INT,
                    "a",
                    analyzedBinaryOp(
                        BinaryOperationType.ADD,
                        analyzedIntLiteral(5, Type.INT),
                        analyzedBinaryOp(
                            BinaryOperationType.MULT,
                            analyzedIntLiteral(10, Type.INT),
                            analyzedIntLiteral(2, Type.INT),
                            Type.INT
                        ),
                        Type.INT
                    )
                ))
                .statement(analyzedVarDecl(
                    Type.INT,
                    "b",
                    analyzedBinaryOp(
                        BinaryOperationType.MULT,
                        analyzedBinaryOp(
                            BinaryOperationType.ADD,
                            analyzedIntLiteral(5, Type.INT),
                            analyzedIntLiteral(10, Type.INT),
                            Type.INT
                        ),
                        analyzedIntLiteral(2, Type.INT),
                        Type.INT
                    )
                ))
                .statement(analyzedVarDecl(
                    Type.BOOL,
                    "c",
                    analyzedBinaryOp(
                        BinaryOperationType.LOGICAL_AND,
                        analyzedBinaryOp(
                            BinaryOperationType.GREATER,
                            analyzedVarExpr("a", Type.INT),
                            analyzedVarExpr("b", Type.INT),
                            Type.BOOL
                        ),
                        analyzedBinaryOp(
                            BinaryOperationType.LESS,
                            analyzedVarExpr("b", Type.INT),
                            analyzedIntLiteral(100, Type.INT),
                            Type.BOOL
                        ),
                        Type.BOOL
                    )
                ))
                .statement(analyzedReturnStmt(
                    analyzedBinaryOp(
                        BinaryOperationType.ADD,
                        analyzedVarExpr("a", Type.INT),
                        analyzedVarExpr("b", Type.INT),
                        Type.INT
                    )
                ))
                .hasGuaranteedReturn(true)
                .localVar(0, "a", Type.INT)
                .localVar(1, "b", Type.INT)
                .localVar(2, "c", Type.BOOL)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void literal_types() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("literal_types.lux");

        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedVarDecl(Type.INT, "i", analyzedIntLiteral(42, Type.INT)))
                .statement(analyzedVarDecl(Type.LONG, "l", analyzedIntLiteral(42L, Type.LONG)))
                .statement(analyzedVarDecl(Type.FLOAT, "f", analyzedFPLiteral(3.14F, Type.FLOAT)))
                .statement(analyzedVarDecl(Type.DOUBLE, "d", analyzedFPLiteral(3.14, Type.DOUBLE)))
                .statement(analyzedVarDecl(Type.BOOL, "b", analyzedBoolLiteral(true)))
                .statement(analyzedReturnStmt(analyzedVarExpr("i", Type.INT)))
                .hasGuaranteedReturn(true)
                .localVar(0, "i", Type.INT)
                .localVar(1, "l", Type.LONG)
                .localVar(2, "f", Type.FLOAT)
                .localVar(3, "d", Type.DOUBLE)
                .localVar(4, "b", Type.BOOL)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void empty_return() throws IOException {
        AnalyzedProgram actual = TestUtils.analyzeFile("empty_return.lux");
        
        AnalyzedProgram expected = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .statement(analyzedReturnStmt())
                .hasGuaranteedReturn(true)
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }
}
