package luxlang.compiler.ir;

import luxlang.compiler.parser.nodes.Type;

import java.util.List;
import java.util.Map;

public record IRFunction(
    String name,
    Type returnType,
    List<Type> parameterTypes,
    Map<String, IRLocal> locals,
    List<BasicBlock> basicBlocks
) {
}
