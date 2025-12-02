package main.ir.values;

import main.parser.nodes.Type;

public record LocalPointer(Type type, int localId) implements IRValue { }
