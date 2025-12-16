package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.parser.nodes.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static luxlang.compiler.ir.IRBuilderTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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
        
        assertEquals(1, module.functions().size());
        IRFunction irFunction = module.functions().get(0);
        assertEquals("main", irFunction.name());
        assertEquals(Type.INT, irFunction.returnType());
        assertEquals(0, irFunction.parameterTypes().size());
        assertEquals(0, irFunction.locals().size());
        assertTrue(irFunction.basicBlocks().size() >= 1);
        
        // Entry block should exist with a terminator
        BasicBlock entryBlock = irFunction.basicBlocks().get(0);
        assertNotNull(entryBlock);
        assertNotNull(entryBlock.terminator());
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
        
        IRFunction irFunction = module.functions().get(0);
        assertEquals(1, irFunction.locals().size());
        assertTrue(irFunction.locals().containsKey("x"));
        
        IRLocal local = irFunction.locals().get("x");
        assertEquals("x", local.name());
        assertEquals(Type.INT, local.type());
        assertEquals(0, local.index());
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
        
        IRFunction irFunction = module.functions().get(0);
        assertEquals(2, irFunction.locals().size());
        assertTrue(irFunction.locals().containsKey("x"));
        assertTrue(irFunction.locals().containsKey("y"));
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
        
        assertEquals(2, module.functions().size());
        assertEquals("foo", module.functions().get(0).name());
        assertEquals("bar", module.functions().get(1).name());
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
        
        IRFunction irFunction = module.functions().get(0);
        assertEquals("empty", irFunction.name());
        assertEquals(Type.VOID, irFunction.returnType());
        assertEquals(0, irFunction.locals().size());
        assertTrue(irFunction.basicBlocks().size() >= 1);
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
        
        IRFunction irFunction = module.functions().get(0);
        List<BasicBlock> blocks = irFunction.basicBlocks();
        
        // Should have at least one basic block (entry)
        assertTrue(blocks.size() >= 1);
        
        // Entry block should have a name
        BasicBlock entryBlock = blocks.get(0);
        assertNotNull(entryBlock.name());
        
        // Entry block should have a terminator
        assertNotNull(entryBlock.terminator());
    }
}
