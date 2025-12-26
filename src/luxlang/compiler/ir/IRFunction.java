package luxlang.compiler.ir;

import luxlang.compiler.parser.nodes.Type;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record IRFunction(
    String name,
    Type returnType,
    List<Type> parameterTypes,
    Map<String, IRLocal> locals,
    List<BasicBlock> basicBlocks
) {
    /**
     * Format:
     *   define <returnType> @<name>(<paramTypes...>) {
     *       local <locals...>
     *
     *       <basicBlocks...>
     *   }
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();

        sb.append("define ").append(returnType.lexeme()).append(" @").append(name).append("(");
        sb.append(parameterTypes.stream()
                .map(Type::lexeme)
                .collect(Collectors.joining(", ")));
        sb.append(") {\n");

        if (!locals.isEmpty()) {
            locals.values().stream()
                    .sorted(Comparator.comparingInt(IRLocal::index))
                    .forEach(local -> sb.append("    ")
                            .append(local.serialize())
                            .append("\n"));
            sb.append("\n");
        }

        for (BasicBlock block : basicBlocks) {
            sb.append(block.serialize());
        }

        sb.append("}");
        return sb.toString();
    }
}
