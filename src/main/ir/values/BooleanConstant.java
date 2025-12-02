package main.ir.values;

import main.parser.nodes.Type;

public record BooleanConstant(Type type, boolean value) implements IRValue { }