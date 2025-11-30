package main.analysis;

import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Parameter;
import main.parser.nodes.Type;

import java.util.List;

import static main.util.StringUtils.typeListAsString;

public record FunctionSymbol(
        String name,
        Type returnType,
        List<Type> parameterTypes
) implements Symbol {
    public static FunctionSymbol from(FunctionDeclaration functionDeclaration) {
        List<Type> parameterTypes = functionDeclaration.parameters()
                .stream()
                .map(Parameter::type)
                .toList();
        return new FunctionSymbol(functionDeclaration.name(), functionDeclaration.returnType(), parameterTypes);
    }

    public String signatureString() {
        return name + "(" + typeListAsString(parameterTypes) + ")";
    }
}
