package main.parser.objects;

import main.lexer.objects.Token;

public record SourceInfo(
    Token firstToken,
    Token lastToken
) { }