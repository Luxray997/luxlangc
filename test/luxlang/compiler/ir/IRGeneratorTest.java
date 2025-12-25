package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static luxlang.compiler.utils.AnalyzedAstBuilder.*;
import static org.assertj.core.api.Assertions.*;

public class IRGeneratorTest {

    @Test
    public void simple_function() {
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedReturnStmt(analyzedIntLiteral(0, Type.INT)))
                .build()
        );

        String expected = """
            define int @main() {
              bb0:
                ret 0
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void function_with_local_variable() {
        // int main() { int x = 42; return x; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .localVar(0, "x", Type.INT)
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(42, Type.INT)))
                .statement(analyzedReturnStmt(analyzedVarExpr("x", Type.INT)))
                .build()
        );

        String expected = """
            define int @main() {
                local %l0 : int
            
              bb0:
                store 42, %l0
                ret %l0
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void function_with_multiple_locals() {
        // int main() { int x = 10; int y = 20; return 0; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .localVar(0, "x", Type.INT)
                .localVar(1, "y", Type.INT)
                .statement(analyzedVarDecl(Type.INT, "x", analyzedIntLiteral(10, Type.INT)))
                .statement(analyzedVarDecl(Type.INT, "y", analyzedIntLiteral(20, Type.INT)))
                .statement(analyzedReturnStmt(analyzedIntLiteral(0, Type.INT)))
                .build()
        );

        String expected = """
            define int @main() {
                local %l0 : int
                local %l1 : int
            
              bb0:
                store 10, %l0
                store 20, %l1
                ret 0
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void multiple_functions() {
        // int foo() { return 1; } int bar() { return 2; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("foo")
                .statement(analyzedReturnStmt(analyzedIntLiteral(1, Type.INT)))
                .build(),
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("bar")
                .statement(analyzedReturnStmt(analyzedIntLiteral(2, Type.INT)))
                .build()
        );

        String expected = """
            define int @foo() {
              bb0:
                ret 1
            }
            
            define int @bar() {
              bb0:
                ret 2
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void void_function() {
        // void empty() { return; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.VOID)
                .name("empty")
                .statement(analyzedReturnStmt())
                .build()
        );

        String expected = """
            define void @empty() {
              bb0:
                ret void
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void function_with_parameters() {
        // int add(int a, int b) { return a + b; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .localVar(0, "a", Type.INT)
                .localVar(1, "b", Type.INT)
                .statement(analyzedReturnStmt(analyzedBinaryOp(
                    BinaryOperationType.ADD,
                    analyzedVarExpr("a", Type.INT),
                    analyzedVarExpr("b", Type.INT),
                    Type.INT
                )))
                .build()
        );

        String expected = """
            define int @add(int, int) {
                local %l0 : int
                local %l1 : int
            
              bb0:
                %t0 = add %l0, %l1
                ret %t0
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void arithmetic_operations() {
        // int main() { return 10 + 20 - 5; }
        AnalyzedProgram input = analyzedProgram(
            analyzedFunctionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(analyzedReturnStmt(analyzedBinaryOp(
                    BinaryOperationType.SUB,
                    analyzedBinaryOp(
                        BinaryOperationType.ADD,
                        analyzedIntLiteral(10, Type.INT),
                        analyzedIntLiteral(20, Type.INT),
                        Type.INT
                    ),
                    analyzedIntLiteral(5, Type.INT),
                    Type.INT
                )))
                .build()
        );

        String expected = """
            define int @main() {
              bb0:
                %t0 = add 10, 20
                %t1 = sub %t0, 5
                ret %t1
            }""";

        IRGenerator builder = new IRGenerator(input);
        IRModule actual = builder.generate();

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }
}
