package main.ir;

import main.parser.nodes.Type;

import java.util.List;

public record IRFunction(
    String name,
    Type returnType,
    List<Type> parameterTypes,
    List<IRLocal> locals,
    List<BasicBlock> basicBlocks
) {
}
