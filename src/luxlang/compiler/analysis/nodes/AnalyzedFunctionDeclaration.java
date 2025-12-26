package luxlang.compiler.analysis.nodes;

import luxlang.compiler.analysis.nodes.statements.AnalyzedCodeBlock;
import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.FunctionDeclaration;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Type;

import java.util.List;

public record AnalyzedFunctionDeclaration(
    Type returnType,
    String name,
    List<Parameter> parameters,
    AnalyzedCodeBlock body,
    List<LocalVariable> localVariables,
    SourceInfo sourceInfo
) {
    public static AnalyzedFunctionDeclaration from(FunctionDeclaration functionDeclaration, AnalyzedCodeBlock body, List<LocalVariable> locals) {
        return new AnalyzedFunctionDeclaration(
            functionDeclaration.returnType(),
            functionDeclaration.name(),
            functionDeclaration.parameters(),
            body,
            locals,
            functionDeclaration.sourceInfo()
        );
    }
}