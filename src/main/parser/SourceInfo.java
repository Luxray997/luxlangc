package main.parser;

import main.lexer.Token;

public record SourceInfo(
    Token firstToken,
    Token lastToken
) { }