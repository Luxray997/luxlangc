package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.parser.objects.SourceInfo;

import java.util.List;

public record AnalyzedCodeBlock(
    List<AnalyzedStatement> statements,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }