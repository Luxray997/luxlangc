package main.ir.values;

import main.parser.nodes.Type;

public record Temporary(Type type, int id) implements IRValue { }
