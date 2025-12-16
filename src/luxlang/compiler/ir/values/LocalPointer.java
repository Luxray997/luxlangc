package luxlang.compiler.ir.values;

import luxlang.compiler.parser.nodes.Type;

public record LocalPointer(Type type, int localId) implements IRValue { }
