package luxlang.compiler.ir.values;

import luxlang.compiler.parser.nodes.Type;

public record Temporary(Type type, int id) implements IRValue {
    /**
     * Format: %t<id>
     */
    @Override
    public String serialize() {
        return "%t" + id;
    }
}
