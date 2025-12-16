package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.analysis.nodes.expressions.AnalyzedIntegerLiteral;
import luxlang.compiler.analysis.nodes.statements.AnalyzedCodeBlock;
import luxlang.compiler.analysis.nodes.statements.AnalyzedReturnStatement;
import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.objects.SourceInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IRBuilderTest {

    private SourceInfo dummySourceInfo() {
        Token dummyToken = new Token(TokenKind.EOF, "", 1, 1);
        return new SourceInfo(dummyToken, dummyToken);
    }

    @Test
    public void simple_function() {
        // int main() { return 0; }
        AnalyzedIntegerLiteral zero = new AnalyzedIntegerLiteral(0, Type.INT, dummySourceInfo());
        AnalyzedReturnStatement returnStmt = new AnalyzedReturnStatement(
            Optional.of(zero),
            dummySourceInfo()
        );
        AnalyzedCodeBlock body = new AnalyzedCodeBlock(
            List.of(returnStmt),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration function = new AnalyzedFunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            List.of(),
            dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(function));
        
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
        LocalVariable varX = new LocalVariable(0, "x", Type.INT);
        
        AnalyzedIntegerLiteral zero = new AnalyzedIntegerLiteral(0, Type.INT, dummySourceInfo());
        AnalyzedReturnStatement returnStmt = new AnalyzedReturnStatement(
            Optional.of(zero),
            dummySourceInfo()
        );
        AnalyzedCodeBlock body = new AnalyzedCodeBlock(
            List.of(returnStmt),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration function = new AnalyzedFunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            List.of(varX),
            dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(function));
        
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
        LocalVariable varX = new LocalVariable(0, "x", Type.INT);
        LocalVariable varY = new LocalVariable(1, "y", Type.INT);
        
        AnalyzedIntegerLiteral zero = new AnalyzedIntegerLiteral(0, Type.INT, dummySourceInfo());
        AnalyzedReturnStatement returnStmt = new AnalyzedReturnStatement(
            Optional.of(zero),
            dummySourceInfo()
        );
        AnalyzedCodeBlock body = new AnalyzedCodeBlock(
            List.of(returnStmt),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration function = new AnalyzedFunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            List.of(varX, varY),
            dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(function));
        
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
        AnalyzedIntegerLiteral one = new AnalyzedIntegerLiteral(1, Type.INT, dummySourceInfo());
        AnalyzedIntegerLiteral two = new AnalyzedIntegerLiteral(2, Type.INT, dummySourceInfo());
        
        AnalyzedReturnStatement returnOne = new AnalyzedReturnStatement(
            Optional.of(one),
            dummySourceInfo()
        );
        AnalyzedReturnStatement returnTwo = new AnalyzedReturnStatement(
            Optional.of(two),
            dummySourceInfo()
        );
        
        AnalyzedCodeBlock body1 = new AnalyzedCodeBlock(
            List.of(returnOne),
            true,
            dummySourceInfo()
        );
        AnalyzedCodeBlock body2 = new AnalyzedCodeBlock(
            List.of(returnTwo),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration func1 = new AnalyzedFunctionDeclaration(
            Type.INT, "foo", List.of(), body1, List.of(), dummySourceInfo()
        );
        AnalyzedFunctionDeclaration func2 = new AnalyzedFunctionDeclaration(
            Type.INT, "bar", List.of(), body2, List.of(), dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(func1, func2));
        
        IRBuilder builder = new IRBuilder(program);
        IRModule module = builder.build();
        
        assertEquals(2, module.functions().size());
        assertEquals("foo", module.functions().get(0).name());
        assertEquals("bar", module.functions().get(1).name());
    }

    @Test
    public void void_function() {
        // void empty() { return; }
        AnalyzedReturnStatement returnStmt = new AnalyzedReturnStatement(
            Optional.empty(),
            dummySourceInfo()
        );
        AnalyzedCodeBlock body = new AnalyzedCodeBlock(
            List.of(returnStmt),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration function = new AnalyzedFunctionDeclaration(
            Type.VOID,
            "empty",
            List.of(),
            body,
            List.of(),
            dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(function));
        
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
        AnalyzedIntegerLiteral zero = new AnalyzedIntegerLiteral(0, Type.INT, dummySourceInfo());
        AnalyzedReturnStatement returnStmt = new AnalyzedReturnStatement(
            Optional.of(zero),
            dummySourceInfo()
        );
        AnalyzedCodeBlock body = new AnalyzedCodeBlock(
            List.of(returnStmt),
            true,
            dummySourceInfo()
        );
        
        AnalyzedFunctionDeclaration function = new AnalyzedFunctionDeclaration(
            Type.INT,
            "main",
            List.of(),
            body,
            List.of(),
            dummySourceInfo()
        );
        
        AnalyzedProgram program = new AnalyzedProgram(List.of(function));
        
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
