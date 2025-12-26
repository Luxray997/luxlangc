package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.expressions.*;
import luxlang.compiler.analysis.nodes.statements.*;
import luxlang.compiler.ir.instructions.*;
import luxlang.compiler.ir.objects.*;
import luxlang.compiler.ir.values.*;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static luxlang.compiler.ir.instructions.Compare.ComparisonType.*;

public class IRGenerator {
    private final AnalyzedProgram program;
    private IRFunction currentFunction;
    private int nextBlockId;
    private int nextTemporaryId;

    public IRGenerator(AnalyzedProgram program) {
        this.program = program;
    }

    record BuiltExpressionResult(IRValue value, BasicBlock lastBlock) { }

    public IRModule generate() {
        List<IRFunction> irFunctions = new ArrayList<>();
        for (var function : program.functionDeclarations()) {
            generateFunction(function);
            irFunctions.add(currentFunction);
        }
        return new IRModule(irFunctions);
    }

    private void generateFunction(AnalyzedFunctionDeclaration function) {
        String name = function.name();
        Type returnType = function.returnType();
        List<Type> parameterTypes = function.parameters().stream()
                .map(Parameter::type)
                .toList();
        Map<String, IRLocal> locals = function.localVariables().stream()
                .map(IRLocal::from)
                .collect(Collectors.toMap(IRLocal::name, identity()));
        List<BasicBlock> basicBlocks = new ArrayList<>();
        currentFunction = new IRFunction(name, returnType, parameterTypes, locals, basicBlocks);
        nextBlockId = 0;
        nextTemporaryId = 0;

        var entryBasicBlock = createEmptyBasicBlock("entry");
        BasicBlock lastBlockInFunction = generateCodeBlock(function.body(), entryBasicBlock);
        lastBlockInFunction.setTerminator(new FunctionReturn(null));
    }

    private BasicBlock generateCodeBlock(AnalyzedCodeBlock codeBlock, BasicBlock precedingBlock) {
        BasicBlock lastBlock = precedingBlock;
        for (var statement : codeBlock.statements()) {
            lastBlock = generateStatement(statement, lastBlock);
        }
        return lastBlock;
    }

    private BasicBlock generateStatement(AnalyzedStatement statement, BasicBlock precedingBlock) {
        return switch (statement) {
            case AnalyzedCodeBlock           nestedCodeBlock     -> generateCodeBlock(nestedCodeBlock, precedingBlock);
            case AnalyzedIfStatement         ifStatement         -> generateIfStatement(ifStatement, precedingBlock);
            case AnalyzedWhileStatement      whileStatement      -> generateWhileStatement(whileStatement, precedingBlock);
            case AnalyzedDoWhileStatement    doWhileStatement    -> generateDoWhileStatement(doWhileStatement, precedingBlock);
            case AnalyzedForStatement        forStatement        -> generateForStatement(forStatement, precedingBlock);
            case AnalyzedReturnStatement     returnStatement     -> generateReturnStatement(returnStatement, precedingBlock);
            case AnalyzedVariableDeclaration variableDeclaration -> generateVariableDeclaration(variableDeclaration, precedingBlock);
            case AnalyzedAssignment          assignment          -> generateAssignment(assignment, precedingBlock);
        };
    }

    private BasicBlock generateAssignment(AnalyzedAssignment assignment, BasicBlock precedingBlock) {
        var valueResult = generateExpression(assignment.value(), precedingBlock);
        int localId = getLocal(assignment.variableName()).index();
        valueResult.lastBlock().instructions().add(new StoreToLocal(localId, valueResult.value()));
        return valueResult.lastBlock();
    }

    private BuiltExpressionResult generateExpression(AnalyzedExpression value, BasicBlock precedingBlock) {
        return switch (value) {
            case AnalyzedBinaryOperation binaryOperation      -> generateBinaryOperation(binaryOperation, precedingBlock);
            case AnalyzedFunctionCall         functionCall         -> new BuiltExpressionResult(generateFunctionCall(functionCall, precedingBlock), precedingBlock);
            case AnalyzedUnaryOperation unaryOperation       -> new BuiltExpressionResult(generateUnaryOperation(unaryOperation, precedingBlock), precedingBlock);
            case AnalyzedVariableExpression variableExpression   -> new BuiltExpressionResult(generateVariableExpression(variableExpression), precedingBlock);
            case AnalyzedFloatingPointLiteral floatingPointLiteral -> new BuiltExpressionResult(FloatingPointConstant.from(floatingPointLiteral), precedingBlock);
            case AnalyzedIntegerLiteral       integerLiteral       -> new BuiltExpressionResult(IntegerConstant.from(integerLiteral), precedingBlock);
            case AnalyzedBooleanLiteral booleanLiteral       -> new BuiltExpressionResult(BooleanConstant.from(booleanLiteral), precedingBlock);
        };
    }

    private IRValue generateVariableExpression(AnalyzedVariableExpression variableExpression) {
        IRLocal local = getLocal(variableExpression.name());
        return new LocalPointer(local.type(), local.index());
    }

    private IRValue generateUnaryOperation(AnalyzedUnaryOperation unaryOperation, BasicBlock precedingBlock) {
        var operandResult = generateExpression(unaryOperation.operand(), precedingBlock);
        Type resultType = unaryOperation.resultType();
        Temporary destination = allocateTemporary(resultType);
        RegularInstruction instruction = switch (unaryOperation.operation()) {
            case LOGICAL_NOT -> new Xor(destination, operandResult.value(), new IntegerConstant(resultType, 1));
            case BITWISE_NOT -> new Not(destination, operandResult.value());
            case NEGATION    -> new Negate(destination, operandResult.value());
        };
        operandResult.lastBlock().instructions().add(instruction);
        return destination;
    }

    private BuiltExpressionResult generateBinaryOperation(AnalyzedBinaryOperation binaryOperation, BasicBlock precedingBlock) {
        if (binaryOperation.operation() == BinaryOperation.BinaryOperationType.LOGICAL_OR) {
            return generateLogicalOr(binaryOperation, precedingBlock);
        }
        if (binaryOperation.operation() == BinaryOperation.BinaryOperationType.LOGICAL_AND) {
            return generateLogicalAnd(binaryOperation, precedingBlock);
        }
        var leftResult = generateExpression(binaryOperation.left(), precedingBlock);
        IRValue left = leftResult.value();
        var rightResult = generateExpression(binaryOperation.right(), leftResult.lastBlock());
        IRValue right = rightResult.value();
        Temporary destination = allocateTemporary(binaryOperation.resultType());
        RegularInstruction instruction = switch (binaryOperation.operation()) {
            case ADD -> new Add(destination, left, right);
            case SUB -> new Subtract(destination, left, right);
            case MULT -> new Multiply(destination, left, right);
            case DIV -> new Divide(destination, left, right);
            case MOD -> new Modulo(destination, left, right);
            case BITWISE_AND -> new And(destination, left, right);
            case BITWISE_OR -> new Or(destination, left, right);
            case BITWISE_XOR -> new Xor(destination, left, right);
            case EQUAL -> new Compare(destination, left, EQUAL, right);
            case NOT_EQUAL -> new Compare(destination, left, NOT_EQUAL, right);
            case LESS -> new Compare(destination, left, LESS, right);
            case LESS_EQUAL -> new Compare(destination, left, LESS_EQUAL, right);
            case GREATER -> new Compare(destination, left, GREATER, right);
            case GREATER_EQUAL -> new Compare(destination, left, GREATER_EQUAL, right);
            case LOGICAL_AND, LOGICAL_OR -> throw new IllegalStateException("Generating logical expression as binary operation");
        };
        rightResult.lastBlock().instructions().add(instruction);
        return new BuiltExpressionResult(destination, rightResult.lastBlock());
    }

    private BuiltExpressionResult generateLogicalAnd(AnalyzedBinaryOperation andOperation, BasicBlock precedingBlock) {
        var leftResult = generateExpression(andOperation.left(), precedingBlock);

        BasicBlock evalRightBlock = createEmptyBasicBlock("eval_right");
        BasicBlock exitBlock = createEmptyBasicBlock("exit");

        leftResult.lastBlock().setTerminator(new ConditionalBranch(leftResult.value(), evalRightBlock, exitBlock));

        var rightResult = generateExpression(andOperation.right(), evalRightBlock);

        rightResult.lastBlock().setTerminator(new UnconditionalBranch(exitBlock));

        Temporary destination = allocateTemporary(Type.BOOL);
        exitBlock.instructions().add(
            new Phi(
                destination,
                leftResult.lastBlock(), leftResult.value(),
                rightResult.lastBlock(), rightResult.value()
            )
        );

        return new BuiltExpressionResult(destination, exitBlock);
    }

    private BuiltExpressionResult generateLogicalOr(AnalyzedBinaryOperation orOperation, BasicBlock precedingBlock) {
        var leftResult = generateExpression(orOperation.left(), precedingBlock);

        BasicBlock evalRightBlock = createEmptyBasicBlock("eval_right");
        BasicBlock exitBlock = createEmptyBasicBlock("exit");

        leftResult.lastBlock().setTerminator(new ConditionalBranch(leftResult.value(), exitBlock, evalRightBlock));

        var rightResult = generateExpression(orOperation.right(), evalRightBlock);

        rightResult.lastBlock().setTerminator(new UnconditionalBranch(exitBlock));

        Temporary destination = allocateTemporary(Type.BOOL);
        exitBlock.instructions().add(
            new Phi(
                destination,
                leftResult.lastBlock(), leftResult.value(),
                rightResult.lastBlock(), rightResult.value()
            )
        );

        return new BuiltExpressionResult(destination, exitBlock);
    }

    private IRValue generateFunctionCall(AnalyzedFunctionCall functionCall, BasicBlock precedingBlock) {
        BasicBlock lastBlock = precedingBlock;
        List<IRValue> argumentValues = new ArrayList<>();
        for (var argument : functionCall.arguments()) {
            var argumentResult = generateExpression(argument, lastBlock);
            lastBlock = argumentResult.lastBlock();
            argumentValues.add(argumentResult.value());
        }
        Temporary destination = allocateTemporary(functionCall.resultType());
        lastBlock.instructions().add(new FunctionCallInstruction(functionCall.name(), destination, argumentValues));
        return destination;
    }


    private BasicBlock generateVariableDeclaration(AnalyzedVariableDeclaration variableDeclaration, BasicBlock precedingBlock) {
        int id = getLocal(variableDeclaration.name()).index();
        if (variableDeclaration.initialValue().isEmpty()) {
            return precedingBlock;
        }

        var initialValueResult = generateExpression(variableDeclaration.initialValue().get(), precedingBlock);
        var lastBlock = initialValueResult.lastBlock();
        var initialValue = initialValueResult.value();
        lastBlock.instructions().add(new StoreToLocal(id, initialValue));
        return lastBlock;
    }

    private BasicBlock generateReturnStatement(AnalyzedReturnStatement returnStatement, BasicBlock precedingBlock) {
        IRValue returnValue = null;
        BasicBlock lastBlock = precedingBlock;
        if (returnStatement.value().isPresent()) {
            var returnValueResult = generateExpression(returnStatement.value().get(), precedingBlock);
            returnValue = returnValueResult.value();
            lastBlock = returnValueResult.lastBlock();
        }
        lastBlock.setTerminator(new FunctionReturn(returnValue));
        return lastBlock;
    }


    private BasicBlock generateForStatement(AnalyzedForStatement forStatement, BasicBlock precedingBlock) {
        BasicBlock lastBlockBeforeEntry = precedingBlock;
        if (forStatement.initializer().isPresent()) {
            lastBlockBeforeEntry = switch (forStatement.initializer().get()) {
                case AnalyzedVariableDeclaration initVariable -> generateVariableDeclaration(initVariable, precedingBlock);
                case AnalyzedAssignment          assignment   -> generateAssignment(assignment, precedingBlock);
            };
        }

        BasicBlock firstBlockInBody = createEmptyBasicBlock("for_body");
        BasicBlock lastBlockInBody = generateStatement(forStatement.body(), firstBlockInBody);

        if (forStatement.update().isPresent()) {
            lastBlockInBody = generateAssignment(forStatement.update().get(), lastBlockInBody);
        }


        BasicBlock entry = firstBlockInBody;
        BasicBlock blockAfterLoop;

        if (forStatement.condition().isPresent()) {
            BasicBlock condition = createEmptyBasicBlock("for_condition");
            var conditionResult = generateExpression(forStatement.condition().get(), condition);

            entry = condition;
            blockAfterLoop = createEmptyBasicBlock("for_exit");

            conditionResult.lastBlock().setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));
        } else {
            blockAfterLoop = createEmptyBasicBlock("for_exit");
        }

        lastBlockBeforeEntry.setTerminator(new UnconditionalBranch(entry));
        lastBlockInBody.setTerminator(new UnconditionalBranch(entry));
        return blockAfterLoop;
    }

    private BasicBlock generateDoWhileStatement(AnalyzedDoWhileStatement doWhileStatement, BasicBlock precedingBlock) {
        BasicBlock firstBlockInCondition = createEmptyBasicBlock("do_while_condition");
        var conditionResult = generateExpression(doWhileStatement.condition(), firstBlockInCondition);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("do_while_body");
        BasicBlock lastBlockInBody = generateStatement(doWhileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("do_while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(firstBlockInBody));
        lastBlockInBody.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock generateWhileStatement(AnalyzedWhileStatement whileStatement, BasicBlock precedingBlock) {
        BasicBlock firstBlockInCondition = createEmptyBasicBlock("while_condition");
        var conditionResult = generateExpression(whileStatement.condition(), firstBlockInCondition);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("while_body");
        BasicBlock lastBlockInBody = generateStatement(whileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInBody.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock generateIfStatement(AnalyzedIfStatement ifStatement, BasicBlock precedingBlock) {
        var conditionResult = generateExpression(ifStatement.condition(), precedingBlock);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("if_body");
        BasicBlock lastBlockInBody = generateStatement(ifStatement.body(), firstBlockInBody);

        if (ifStatement.elseBody().isPresent()) {
            BasicBlock firstBlockInElseBody = createEmptyBasicBlock("else_body");

            BasicBlock lastBlockInElseBody = generateStatement(ifStatement.elseBody().get(), firstBlockInElseBody);

            lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, firstBlockInElseBody));

            if (ifStatement.hasGuaranteedReturn()) {
                // No need to create a merge block or set terminators if both branches return
                return lastBlockInElseBody;
            }

            BasicBlock mergeBlock = createEmptyBasicBlock("merge");
            lastBlockInBody.setTerminator(new UnconditionalBranch(mergeBlock));
            lastBlockInElseBody.setTerminator(new UnconditionalBranch(mergeBlock));
            return mergeBlock;
        }

        BasicBlock mergeBlock = createEmptyBasicBlock("merge");

        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, mergeBlock));
        lastBlockInBody.setTerminator(new UnconditionalBranch(mergeBlock));

        return mergeBlock;
    }

    private BasicBlock createEmptyBasicBlock(String name) {
        var block = new BasicBlock(nextBlockId, name);
        nextBlockId++;
        currentFunction.basicBlocks().add(block);
        return block;
    }

    private IRLocal getLocal(String name) {
        return currentFunction.locals().get(name);
    }

    private Temporary allocateTemporary(Type type) {
        return new Temporary(type, nextTemporaryId++);
    }
}
