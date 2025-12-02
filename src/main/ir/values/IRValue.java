package main.ir.values;

import main.parser.nodes.Type;

public sealed interface IRValue permits Temporary, LocalPointer, IntegerConstant, FloatingPointConstant, BooleanConstant {
    Type type();
}
