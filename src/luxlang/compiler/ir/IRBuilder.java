package luxlang.compiler.ir;

import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.expressions.*;
import luxlang.compiler.analysis.nodes.statements.*;
import luxlang.compiler.ir.instructions.*;
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
// TODO: good unit tests
public class IRBuilder {
    private final AnalyzedProgram program;
    private IRFunction currentFunction;
    private int nextBlockId;
    private int nextTemporaryId;

    public IRBuilder(AnalyzedProgram program) {
        this.program = program;
    }

    public IRModule build() {
        List<IRFunction> irFunctions = new ArrayList<>();
        for (var function : program.functionDeclarations()) {
            buildFunction(function);
            irFunctions.add(currentFunction);
        }
        return new IRModule(irFunctions);
    }

    private void buildFunction(AnalyzedFunctionDeclaration function) {
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
        BasicBlock lastBlockInFunction = buildCodeBlock(function.body(), entryBasicBlock);
        lastBlockInFunction.setTerminator(new FunctionReturn(null));
    }

    private BasicBlock buildCodeBlock(AnalyzedCodeBlock codeBlock, BasicBlock precedingBlock) {
        BasicBlock lastBlock = precedingBlock;
        for (var statement : codeBlock.statements()) {
            lastBlock = buildStatement(statement, lastBlock);
        }
        return lastBlock;
    }

    private BasicBlock buildStatement(AnalyzedStatement statement, BasicBlock precedingBlock) {
        return switch (statement) {
            case AnalyzedCodeBlock           nestedCodeBlock     -> buildCodeBlock(nestedCodeBlock, precedingBlock);
            case AnalyzedIfStatement         ifStatement         -> buildIfStatement(ifStatement, precedingBlock);
            case AnalyzedWhileStatement whileStatement      -> buildWhileStatement(whileStatement, precedingBlock);
            case AnalyzedDoWhileStatement doWhileStatement    -> buildDoWhileStatement(doWhileStatement, precedingBlock);
            case AnalyzedForStatement        forStatement        -> buildForStatement(forStatement, precedingBlock);
            case AnalyzedReturnStatement     returnStatement     -> buildReturnStatement(returnStatement, precedingBlock);
            case AnalyzedVariableDeclaration variableDeclaration -> buildVariableDeclaration(variableDeclaration, precedingBlock);
            case AnalyzedAssignment assignment          -> buildAssignment(assignment, precedingBlock);
        };
    }

    private BasicBlock buildAssignment(AnalyzedAssignment assignment, BasicBlock precedingBlock) {
        var valueResult = buildExpression(assignment.value(), precedingBlock);
        int localId = getLocal(assignment.variableName()).index();
        valueResult.lastBlock().instructions().add(new StoreToLocal(localId, valueResult.value()));
        return valueResult.lastBlock();
    }

    private BuiltExpressionResult buildExpression(AnalyzedExpression value, BasicBlock precedingBlock) {
        return switch (value) {
            case AnalyzedBinaryOperation binaryOperation      -> buildBinaryOperation(binaryOperation, precedingBlock);
            case AnalyzedFunctionCall         functionCall         -> new BuiltExpressionResult(buildFunctionCall(functionCall, precedingBlock), precedingBlock);
            case AnalyzedUnaryOperation unaryOperation       -> new BuiltExpressionResult(buildUnaryOperation(unaryOperation, precedingBlock), precedingBlock);
            case AnalyzedVariableExpression variableExpression   -> new BuiltExpressionResult(buildVariableExpression(variableExpression), precedingBlock);
            case AnalyzedFloatingPointLiteral floatingPointLiteral -> new BuiltExpressionResult(FloatingPointConstant.from(floatingPointLiteral), precedingBlock);
            case AnalyzedIntegerLiteral       integerLiteral       -> new BuiltExpressionResult(IntegerConstant.from(integerLiteral), precedingBlock);
            case AnalyzedBooleanLiteral booleanLiteral       -> new BuiltExpressionResult(BooleanConstant.from(booleanLiteral), precedingBlock);
        };
    }

    private IRValue buildVariableExpression(AnalyzedVariableExpression variableExpression) {
        IRLocal local = getLocal(variableExpression.name());
        return new LocalPointer(local.type(), local.index());
    }

    private IRValue buildUnaryOperation(AnalyzedUnaryOperation unaryOperation, BasicBlock precedingBlock) {
        var operandResult = buildExpression(unaryOperation.operand(), precedingBlock);
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

    private BuiltExpressionResult buildBinaryOperation(AnalyzedBinaryOperation binaryOperation, BasicBlock precedingBlock) {
        if (binaryOperation.operation() == BinaryOperation.BinaryOperationType.LOGICAL_OR) {
            return buildLogicalOr(binaryOperation, precedingBlock);
        }
        if (binaryOperation.operation() == BinaryOperation.BinaryOperationType.LOGICAL_AND) {
            return buildLogicalAnd(binaryOperation, precedingBlock);
        }
        var leftResult = buildExpression(binaryOperation.left(), precedingBlock);
        IRValue left = leftResult.value();
        var rightResult = buildExpression(binaryOperation.right(), leftResult.lastBlock());
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
            case LOGICAL_AND, LOGICAL_OR -> throw new IllegalStateException("Building logical expression as binary operation");
        };
        rightResult.lastBlock().instructions().add(instruction);
        return new BuiltExpressionResult(destination, rightResult.lastBlock());
    }

    private BuiltExpressionResult buildLogicalAnd(AnalyzedBinaryOperation andOperation, BasicBlock precedingBlock) {
        var leftResult = buildExpression(andOperation.left(), precedingBlock);

        BasicBlock evalRightBlock = createEmptyBasicBlock("eval_right");
        BasicBlock exitBlock = createEmptyBasicBlock("exit");

        leftResult.lastBlock().setTerminator(new ConditionalBranch(leftResult.value(), evalRightBlock, exitBlock));

        var rightResult = buildExpression(andOperation.right(), evalRightBlock);

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

    private BuiltExpressionResult buildLogicalOr(AnalyzedBinaryOperation orOperation, BasicBlock precedingBlock) {
        var leftResult = buildExpression(orOperation.left(), precedingBlock);

        BasicBlock evalRightBlock = createEmptyBasicBlock("eval_right");
        BasicBlock exitBlock = createEmptyBasicBlock("exit");

        leftResult.lastBlock().setTerminator(new ConditionalBranch(leftResult.value(), exitBlock, evalRightBlock));

        var rightResult = buildExpression(orOperation.right(), evalRightBlock);

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

    private IRValue buildFunctionCall(AnalyzedFunctionCall functionCall, BasicBlock precedingBlock) {
        BasicBlock lastBlock = precedingBlock;
        List<IRValue> argumentValues = new ArrayList<>();
        for (var argument : functionCall.arguments()) {
            var argumentResult = buildExpression(argument, lastBlock);
            lastBlock = argumentResult.lastBlock();
            argumentValues.add(argumentResult.value());
        }
        Temporary destination = allocateTemporary(functionCall.resultType());
        lastBlock.instructions().add(new FunctionCallInstruction(functionCall.name(), destination, argumentValues));
        return destination;
    }


    private BasicBlock buildVariableDeclaration(AnalyzedVariableDeclaration variableDeclaration, BasicBlock precedingBlock) {
        int id = getLocal(variableDeclaration.name()).index();
        if (variableDeclaration.initialValue().isEmpty()) {
            return precedingBlock;
        }

        var initialValueResult = buildExpression(variableDeclaration.initialValue().get(), precedingBlock);
        var lastBlock = initialValueResult.lastBlock();
        var initialValue = initialValueResult.value();
        lastBlock.instructions().add(new StoreToLocal(id, initialValue));
        return lastBlock;
    }

    private BasicBlock buildReturnStatement(AnalyzedReturnStatement returnStatement, BasicBlock precedingBlock) {
        IRValue returnValue = null;
        BasicBlock lastBlock = precedingBlock;
        if (returnStatement.value().isPresent()) {
            var returnValueResult = buildExpression(returnStatement.value().get(), precedingBlock);
            returnValue = returnValueResult.value();
            lastBlock = returnValueResult.lastBlock();
        }
        lastBlock.setTerminator(new FunctionReturn(returnValue));
        return lastBlock;
    }


    private BasicBlock buildForStatement(AnalyzedForStatement forStatement, BasicBlock precedingBlock) {
        BasicBlock lastBlockBeforeEntry = precedingBlock;
        if (forStatement.initializer().isPresent()) {
            lastBlockBeforeEntry = switch (forStatement.initializer().get()) {
                case AnalyzedVariableDeclaration initVariable -> buildVariableDeclaration(initVariable, precedingBlock);
                case AnalyzedAssignment          assignment   -> buildAssignment(assignment, precedingBlock);
            };
        }

        BasicBlock firstBlockInBody = createEmptyBasicBlock("for_body");
        BasicBlock lastBlockInBody = buildStatement(forStatement.body(), firstBlockInBody);

        if (forStatement.update().isPresent()) {
            lastBlockInBody = buildAssignment(forStatement.update().get(), lastBlockInBody);
        }

        BasicBlock blockAfterLoop = createEmptyBasicBlock("for_exit");

        BasicBlock entry = firstBlockInBody;

        if (forStatement.condition().isPresent()) {
            BasicBlock condition = createEmptyBasicBlock("for_condition");
            var conditionResult = buildExpression(forStatement.condition().get(), condition);

            entry = condition;

            conditionResult.lastBlock().setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));
        }

        lastBlockBeforeEntry.setTerminator(new UnconditionalBranch(entry));
        lastBlockInBody.setTerminator(new UnconditionalBranch(entry));
        return blockAfterLoop;
    }

    private BasicBlock buildDoWhileStatement(AnalyzedDoWhileStatement doWhileStatement, BasicBlock precedingBlock) {
        BasicBlock firstBlockInCondition = createEmptyBasicBlock("do_while_condition");
        var conditionResult = buildExpression(doWhileStatement.condition(), firstBlockInCondition);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("do_while_body");
        BasicBlock lastBlockInBody = buildStatement(doWhileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("do_while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(firstBlockInBody));
        lastBlockInBody.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock buildWhileStatement(AnalyzedWhileStatement whileStatement, BasicBlock precedingBlock) {
        BasicBlock firstBlockInCondition = createEmptyBasicBlock("while_condition");
        var conditionResult = buildExpression(whileStatement.condition(), firstBlockInCondition);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("while_body");
        BasicBlock lastBlockInBody = buildStatement(whileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInBody.setTerminator(new UnconditionalBranch(firstBlockInCondition));
        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock buildIfStatement(AnalyzedIfStatement ifStatement, BasicBlock precedingBlock) {
        var conditionResult = buildExpression(ifStatement.condition(), precedingBlock);
        BasicBlock lastBlockInCondition = conditionResult.lastBlock();

        BasicBlock firstBlockInBody = createEmptyBasicBlock("if_body");
        BasicBlock mergeBlock = createEmptyBasicBlock("merge");

        BasicBlock lastBlockInBody = buildStatement(ifStatement.body(), firstBlockInBody);

        BasicBlock notTakenTarget = mergeBlock;
        if (ifStatement.elseBody().isPresent()) {
            BasicBlock firstBlockInElseBody = createEmptyBasicBlock("else_body");
            notTakenTarget = firstBlockInElseBody;

            BasicBlock lastBlockInElseBody = buildStatement(ifStatement.elseBody().get(), firstBlockInElseBody);
            lastBlockInElseBody.setTerminator(new UnconditionalBranch(mergeBlock));
        }

        lastBlockInCondition.setTerminator(new ConditionalBranch(conditionResult.value(), firstBlockInBody, notTakenTarget));
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
