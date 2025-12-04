package main.analysis.nodes.statements;

import java.util.List;

public record AnalyzedCodeBlock(
    List<AnalyzedStatement> statements,
    boolean hasGuaranteedReturn
) implements AnalyzedStatement { }