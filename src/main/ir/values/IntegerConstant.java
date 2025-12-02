package main.ir.values;

import main.parser.nodes.Type;

public record IntegerConstant(Type type, long value) implements IRValue {
}
