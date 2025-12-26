package luxlang.compiler.parser.objects;

import luxlang.compiler.lexer.objects.Token;

public record SourceInfo(
    Token firstToken,
    Token lastToken
) { }