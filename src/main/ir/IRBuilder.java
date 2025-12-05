package main.ir;

import main.analysis.nodes.AnalyzedFunctionDeclaration;
import main.analysis.nodes.AnalyzedProgram;
import main.analysis.nodes.expressions.AnalyzedExpression;
import main.analysis.nodes.statements.*;
import main.ir.instructions.*;
import main.ir.values.*;
import main.parser.nodes.Parameter;
import main.parser.nodes.Type;
import main.analysis.nodes.expressions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static main.ir.instructions.Compare.ComparisonType.*;
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
            case AnalyzedWhileStatement      whileStatement      -> buildWhileStatement(whileStatement, precedingBlock);
            case AnalyzedDoWhileStatement    doWhileStatement    -> buildDoWhileStatement(doWhileStatement, precedingBlock);
            case AnalyzedForStatement        forStatement        -> buildForStatement(forStatement, precedingBlock);
            case AnalyzedReturnStatement     returnStatement     -> buildReturnStatement(returnStatement, precedingBlock);
            case AnalyzedVariableDeclaration variableDeclaration -> buildVariableDeclaration(variableDeclaration, precedingBlock);
            case AnalyzedAssignment          assignment          -> buildAssignment(assignment, precedingBlock);
        };
    }

    private BasicBlock buildAssignment(AnalyzedAssignment assignment, BasicBlock precedingBlock) {
        IRValue value = buildExpression(assignment.value(), precedingBlock);
        int localId = getLocal(assignment.variableName()).index();
        precedingBlock.instructions().add(new StoreToLocal(localId, value));
        return precedingBlock;
    }

    private IRValue buildExpression(AnalyzedExpression value, BasicBlock precedingBlock) {
        return switch (value) {
            case AnalyzedFunctionCall         functionCall         -> buildFunctionCall(functionCall, precedingBlock);
            case AnalyzedBinaryOperation      binaryOperation      -> buildBinaryOperation(binaryOperation, precedingBlock);
            case AnalyzedUnaryOperation       unaryOperation       -> buildUnaryOperation(unaryOperation, precedingBlock);
            case AnalyzedVariableExpression   variableExpression   -> buildVariableExpression(variableExpression);
            case AnalyzedFloatingPointLiteral floatingPointLiteral -> FloatingPointConstant.from(floatingPointLiteral);
            case AnalyzedIntegerLiteral       integerLiteral       -> IntegerConstant.from(integerLiteral);
            case AnalyzedBooleanLiteral       booleanLiteral       -> BooleanConstant.from(booleanLiteral);
        };
    }

    private IRValue buildVariableExpression(AnalyzedVariableExpression variableExpression) {
        IRLocal local = getLocal(variableExpression.name());
        return new LocalPointer(local.type(), local.index());
    }

    private IRValue buildUnaryOperation(AnalyzedUnaryOperation unaryOperation, BasicBlock precedingBlock) {
        IRValue operand = buildExpression(unaryOperation.operand(), precedingBlock);
        Type resultType = unaryOperation.resultType();
        Temporary destination = allocateTemporary(resultType);
        RegularInstruction instruction = switch (unaryOperation.operation()) {
            case LOGICAL_NOT -> new Xor(destination, operand, new IntegerConstant(resultType, 1));
            case BITWISE_NOT -> new Not(destination, operand);
            case NEGATION -> new Negate(destination, operand);
        };
        precedingBlock.instructions().add(instruction);
        return destination;
    }

    private IRValue buildBinaryOperation(AnalyzedBinaryOperation binaryOperation, BasicBlock precedingBlock) {
        IRValue left = buildExpression(binaryOperation.left(), precedingBlock);
        IRValue right = buildExpression(binaryOperation.right(), precedingBlock);
        Temporary destination = allocateTemporary(binaryOperation.resultType());
        RegularInstruction instruction = switch (binaryOperation.operation()) {
            case ADD -> new Add(destination, left, right);
            case SUB -> new Subtract(destination, left, right);
            case MULT -> new Multiply(destination, left, right);
            case DIV -> new Divide(destination, left, right);
            case MOD -> new Modulo(destination, left, right);
            case LOGICAL_AND -> new And(destination, left, right); // TODO: implement short-circuiting
            case LOGICAL_OR -> new Or(destination, left, right);
            case BITWISE_AND -> new And(destination, left, right);
            case BITWISE_OR -> new Or(destination, left, right);
            case BITWISE_XOR -> new Xor(destination, left, right);
            case EQUAL -> new Compare(EQUAL, destination, left, right);
            case NOT_EQUAL -> new Compare(NOT_EQUAL, destination, left, right);
            case LESS -> new Compare(LESS, destination, left, right);
            case LESS_EQUAL -> new Compare(LESS_EQUAL, destination, left, right);
            case GREATER -> new Compare(GREATER, destination, left, right);
            case GREATER_EQUAL -> new Compare(GREATER_EQUAL, destination, left, right);
        };
        precedingBlock.instructions().add(instruction);
        return destination;
    }

    private IRValue buildFunctionCall(AnalyzedFunctionCall functionCall, BasicBlock precedingBlock) {
        List<IRValue> argumentValues = functionCall.arguments().stream()
                .map(argument -> buildExpression(argument, precedingBlock))
                .toList();
        Temporary destination = allocateTemporary(functionCall.resultType());
        precedingBlock.instructions().add(new FunctionCallInstruction(functionCall.name(), destination, argumentValues));
        return destination;
    }


    private BasicBlock buildVariableDeclaration(AnalyzedVariableDeclaration variableDeclaration, BasicBlock precedingBlock) {
        int id = getLocal(variableDeclaration.name()).index();
        if (variableDeclaration.initialValue().isPresent()) {
            IRValue initialValue = buildExpression(variableDeclaration.initialValue().get(), precedingBlock);
            precedingBlock.instructions().add(new StoreToLocal(id, initialValue));
        }
        return precedingBlock;
    }

    private BasicBlock buildReturnStatement(AnalyzedReturnStatement returnStatement, BasicBlock precedingBlock) {
        IRValue returnValue = null;
        if (returnStatement.value().isPresent()) {
            returnValue = buildExpression(returnStatement.value().get(), precedingBlock);
        }
        precedingBlock.setTerminator(new FunctionReturn(returnValue));
        return precedingBlock;
    }


    private BasicBlock buildForStatement(AnalyzedForStatement forStatement, BasicBlock precedingBlock) {
        if (forStatement.initializer().isPresent()) {
            switch (forStatement.initializer().get()) {
                case AnalyzedVariableDeclaration initVariable -> buildVariableDeclaration(initVariable, precedingBlock);
                case AnalyzedAssignment          assignment   -> buildAssignment(assignment, precedingBlock);
            }
        }

        BasicBlock firstBlockInBody = createEmptyBasicBlock("for_body");
        BasicBlock lastBlockInBody = buildStatement(forStatement.body(), firstBlockInBody);

        if (forStatement.update().isPresent()) {
            buildAssignment(forStatement.update().get(), lastBlockInBody);
        }

        BasicBlock blockAfterLoop = createEmptyBasicBlock("for_exit");

        BasicBlock entry = firstBlockInBody;


        if (forStatement.condition().isPresent()) {
            BasicBlock condition = createEmptyBasicBlock("for_condition");
            IRValue conditionValue = buildExpression(forStatement.condition().get(), condition);

            entry = condition;

            condition.setTerminator(new ConditionalBranch(conditionValue, firstBlockInBody, blockAfterLoop));
        }

        precedingBlock.setTerminator(new UnconditionalBranch(entry));
        lastBlockInBody.setTerminator(new UnconditionalBranch(entry));
        return blockAfterLoop;
    }

    private BasicBlock buildDoWhileStatement(AnalyzedDoWhileStatement doWhileStatement, BasicBlock precedingBlock) {
        BasicBlock condition = createEmptyBasicBlock("do_while_condition");
        IRValue conditionValue = buildExpression(doWhileStatement.condition(), condition);

        BasicBlock firstBlockInBody = createEmptyBasicBlock("do_while_body");
        BasicBlock lastBlockInBody = buildStatement(doWhileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("do_while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(firstBlockInBody));
        lastBlockInBody.setTerminator(new UnconditionalBranch(condition));
        condition.setTerminator(new ConditionalBranch(conditionValue, firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock buildWhileStatement(AnalyzedWhileStatement whileStatement, BasicBlock precedingBlock) {
        BasicBlock condition = createEmptyBasicBlock("while_condition");
        IRValue conditionValue = buildExpression(whileStatement.condition(), condition);

        BasicBlock firstBlockInBody = createEmptyBasicBlock("while_body");
        BasicBlock lastBlockInBody = buildStatement(whileStatement.body(), firstBlockInBody);

        BasicBlock blockAfterLoop = createEmptyBasicBlock("while_exit");

        precedingBlock.setTerminator(new UnconditionalBranch(condition));
        lastBlockInBody.setTerminator(new UnconditionalBranch(condition));
        condition.setTerminator(new ConditionalBranch(conditionValue, firstBlockInBody, blockAfterLoop));

        return blockAfterLoop;
    }

    private BasicBlock buildIfStatement(AnalyzedIfStatement ifStatement, BasicBlock precedingBlock) {
        IRValue conditionValue = buildExpression(ifStatement.condition(), precedingBlock);
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

        precedingBlock.setTerminator(new ConditionalBranch(conditionValue, firstBlockInBody, notTakenTarget));
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
