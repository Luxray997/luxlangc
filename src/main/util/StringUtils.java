package main.util;

import main.parser.nodes.Type;

import java.util.List;

public class StringUtils {
    public static String typeListAsString(List<Type> types) {
        if (types.isEmpty()) return "";
        var sb = new StringBuilder();
        sb.append(types.getFirst().toString());
        for (int i = 1; i < types.size(); i++) {
            Type type = types.get(i);
            sb.append(", ")
                .append(type.toString());
        }
        return sb.toString();
    }
}
