package main.ir.values;

import main.parser.nodes.Type;

public record FloatingPointConstant(Type type, double value) implements IRValue { }
