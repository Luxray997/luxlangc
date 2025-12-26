package luxlang.compiler.integration;

import luxlang.compiler.ir.objects.IRModule;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IRGeneratorIntegrationTest {

    @Test
    public void simple_function() throws IOException {
        IRModule actual = TestUtils.buildIR("simple_function.lux");

        String expected = """
            define int @main() {
              bb0:
                ret 0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void arithmetic() throws IOException {
        IRModule actual = TestUtils.buildIR("arithmetic.lux");

        String expected = """
            define int @main() {
                local %l0 : int
                local %l1 : int
                local %l2 : int
                local %l3 : int
                local %l4 : int
            
              bb0:
                %t0 = add 10, 20
                store %t0, %l0
                %t1 = sub 30, 15
                store %t1, %l1
                %t2 = mul 5, 4
                store %t2, %l2
                %t3 = div 20, 2
                store %t3, %l3
                %t4 = mod 17, 5
                store %t4, %l4
                %t5 = add %l0, %l1
                %t6 = add %t5, %l2
                %t7 = add %t6, %l3
                %t8 = add %t7, %l4
                ret %t8
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void if_statement() throws IOException {
        IRModule actual = TestUtils.buildIR("if_statement.lux");

        String expected = """
            define int @main() {
                local %l0 : int
            
              bb0:
                store 10, %l0
                %t0 = cmp gt %l0, 5
                br %t0, bb1, bb2
              bb1:
                ret 1
              bb2:
                ret 0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void if_else_statement() throws IOException {
        IRModule actual = TestUtils.buildIR("if_else_statement.lux");

        String expected = """
            define int @main() {
                local %l0 : int
            
              bb0:
                store 10, %l0
                %t0 = cmp gt %l0, 5
                br %t0, bb1, bb2
              bb1:
                ret 1
              bb2:
                ret 0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void while_loop() throws IOException {
        IRModule actual = TestUtils.buildIR("while_loop.lux");

        String expected = """
            define int @main() {
                local %l0 : int
            
              bb0:
                store 0, %l0
                br bb1
              bb1:
                %t0 = cmp lt %l0, 10
                br %t0, bb2, bb3
              bb2:
                %t1 = add %l0, 1
                store %t1, %l0
                br bb1
              bb3:
                ret %l0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void for_loop() throws IOException {
        IRModule actual = TestUtils.buildIR("for_loop.lux");

        String expected = """
            define int @main() {
                local %l0 : int
                local %l1 : int
            
              bb0:
                store 0, %l0
                store 0, %l1
                br bb2
              bb1:
                %t0 = add %l0, %l1
                store %t0, %l0
                %t1 = add %l1, 1
                store %t1, %l1
                br bb2
              bb2:
                %t2 = cmp lt %l1, 10
                br %t2, bb1, bb3
              bb3:
                ret %l0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void local_variables() throws IOException {
        IRModule actual = TestUtils.buildIR("local_variables.lux");

        String expected = """
            define int @main() {
                local %l0 : int
                local %l1 : int
                local %l2 : int
            
              bb0:
                store 10, %l0
                store 20, %l1
                %t0 = add %l0, %l1
                store %t0, %l2
                ret %l2
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void function_parameters() throws IOException {
        IRModule actual = TestUtils.buildIR("function_parameters.lux");

        String expected = """
            define int @add(int, int) {
                local %l0 : int
                local %l1 : int
            
              bb0:
                %t0 = add %l0, %l1
                ret %t0
            }

            define int @main() {
              bb0:
                ret 0
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }

    @Test
    public void empty_function() throws IOException {
        IRModule actual = TestUtils.buildIR("empty_function.lux");

        String expected = """
            define void @empty() {
              bb0:
                ret void
            }""";

        assertThat(TestUtils.normalizeIR(actual.serialize()))
            .isEqualTo(TestUtils.normalizeIR(expected));
    }
}
