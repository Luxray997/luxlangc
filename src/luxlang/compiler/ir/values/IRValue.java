package luxlang.compiler.ir.values;

import luxlang.compiler.parser.nodes.Type;

public sealed interface IRValue permits Temporary, LocalPointer, IntegerConstant, FloatingPointConstant, BooleanConstant {
    Type type();

    String serialize();
}
