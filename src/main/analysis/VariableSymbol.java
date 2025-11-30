package main.analysis;

import main.parser.nodes.Parameter;
import main.parser.nodes.Type;
import main.parser.nodes.statements.VariableDeclaration;

public record VariableSymbol(
    String name,
    Type type
) implements Symbol {
    public static VariableSymbol from(Parameter parameter) {
        return new VariableSymbol(parameter.name(), parameter.type());
    }
    public static VariableSymbol from(VariableDeclaration variableDeclaration) {
        return new VariableSymbol(variableDeclaration.name(), variableDeclaration.type());
    }
}