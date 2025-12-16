package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.parser.nodes.Type;
import org.junit.jupiter.api.Test;

import static luxlang.compiler.ir.IRBuilderTestUtils.*;
import static org.assertj.core.api.Assertions.*;

public class IRBuilderTest {

    @Test
    public void simple_function() {
        // int main() { return 0; }
        AnalyzedProgram program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .returnValue(intLiteral(0))
                .build()
        );
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions())
            .hasSize(1)
            .first()
            .satisfies(irFunction -> {
                assertThat(irFunction.name()).isEqualTo("main");
                assertThat(irFunction.returnType()).isEqualTo(Type.INT);
                assertThat(irFunction.parameterTypes()).isEmpty();
                assertThat(irFunction.locals()).isEmpty();
                assertThat(irFunction.basicBlocks())
                    .isNotEmpty()
                    .first()
                    .satisfies(entryBlock -> {
                        assertThat(entryBlock).isNotNull();
                        assertThat(entryBlock.terminator()).isNotNull();
                    });
            });
    }

    @Test
    public void function_with_local_variables() {
        // int main() { int x = 42; return x; }
        AnalyzedProgram program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .localVar(0, "x", Type.INT)
                .returnValue(intLiteral(0))
                .build()
        );
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions().get(0).locals())
            .hasSize(1)
            .containsKey("x");
        
        assertThat(module.functions().get(0).locals().get("x"))
            .satisfies(local -> {
                assertThat(local.name()).isEqualTo("x");
                assertThat(local.type()).isEqualTo(Type.INT);
                assertThat(local.index()).isEqualTo(0);
            });
    }

    @Test
    public void function_with_multiple_local_variables() {
        // int main() { int x = 10; int y = 20; return 0; }
        AnalyzedProgram program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .localVar(0, "x", Type.INT)
                .localVar(1, "y", Type.INT)
                .returnValue(intLiteral(0))
                .build()
        );
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions().get(0).locals())
            .hasSize(2)
            .containsKeys("x", "y");
    }

    @Test
    public void multiple_functions() {
        // int foo() { return 1; } int bar() { return 2; }
        AnalyzedProgram program = program(
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
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions())
            .hasSize(2)
            .extracting(IRFunction::name)
            .containsExactly("foo", "bar");
    }

    @Test
    public void void_function() {
        // void empty() { return; }
        AnalyzedProgram program = program(
            function()
                .returnType(Type.VOID)
                .name("empty")
                .returnVoid()
                .build()
        );
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions().get(0))
            .satisfies(irFunction -> {
                assertThat(irFunction.name()).isEqualTo("empty");
                assertThat(irFunction.returnType()).isEqualTo(Type.VOID);
                assertThat(irFunction.locals()).isEmpty();
                assertThat(irFunction.basicBlocks()).isNotEmpty();
            });
    }

    @Test
    public void basic_block_structure() {
        // int main() { return 0; }
        AnalyzedProgram program = program(
            function()
                .returnType(Type.INT)
                .name("main")
                .returnValue(intLiteral(0))
                .build()
        );
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertThat(module.functions().get(0).basicBlocks())
            .isNotEmpty()
            .first()
            .satisfies(entryBlock -> {
                assertThat(entryBlock.name()).isNotNull();
                assertThat(entryBlock.terminator()).isNotNull();
            });
    }
}
