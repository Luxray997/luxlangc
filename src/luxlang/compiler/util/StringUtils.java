package luxlang.compiler.util;

import luxlang.compiler.parser.nodes.Type;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    public static String typeListAsString(List<Type> types) {
        return types.stream()
            .map(Type::toString)
            .collect(Collectors.joining(", "));
    }
}
