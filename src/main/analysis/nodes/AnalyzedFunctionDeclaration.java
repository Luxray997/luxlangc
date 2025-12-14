package main.analysis.nodes;

import main.analysis.nodes.statements.AnalyzedCodeBlock;
import main.parser.objects.SourceInfo;
import main.parser.nodes.FunctionDeclaration;
import main.parser.nodes.Parameter;
import main.parser.nodes.Type;

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