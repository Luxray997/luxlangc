package main.analysis.nodes.statements;

import main.parser.objects.SourceInfo;

import java.util.List;

public record AnalyzedCodeBlock(
    List<AnalyzedStatement> statements,
    boolean hasGuaranteedReturn,
    SourceInfo sourceInfo
) implements AnalyzedStatement { }