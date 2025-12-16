package luxlang.compiler.analysis.objects;

import luxlang.compiler.parser.nodes.FunctionDeclaration;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;

import java.util.List;

import static luxlang.compiler.util.StringUtils.typeListAsString;

public record FunctionSymbol(
        String name,
        Type returnType,
        List<Type> parameterTypes
) {
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
