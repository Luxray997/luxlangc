package luxlang.compiler.analysis.objects;

import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;

public record VariableSymbol(
    String name,
    Type type
) {
    public static VariableSymbol from(Parameter parameter) {
        return new VariableSymbol(parameter.name(), parameter.type());
    }
    public static VariableSymbol from(VariableDeclaration variableDeclaration) {
        return new VariableSymbol(variableDeclaration.name(), variableDeclaration.type());
    }
}