package luxlang.compiler.lexer.objects;

public enum TokenKind {
    // Keywords (Types)
    VOID("void"),
    BOOL("bool"),
    BYTE("byte"),
    UBYTE("ubyte"),
    SHORT("short"),
    USHORT("ushort"),
    INT("int"),
    UINT("uint"),
    LONG("long"),
    ULONG("ulong"),
    FLOAT("float"),
    DOUBLE("double"),
    // Keywords (Logical Flow)
    IF("if"),
    ELSE("else"),
    DO("do"),
    WHILE("while"),
    FOR("for"),
    RETURN("return"),
    // Identifiers and Literals
    IDENTIFIER(null),
    LITERAL_INTEGER(null),
    LITERAL_FLOATINGPT(null),
    TRUE("true"),
    FALSE("false"),
    // Operators
    ADD("+"),
    SUB("-"),
    MULT("*"),
    DIV("/"),
    MOD("%"),
    LOGICAL_NOT("!"),
    LOGICAL_AND("&&"),
    LOGICAL_OR("||"),
    BITWISE_NOT("~"),
    BITWISE_AND("&"),
    BITWISE_OR("|"),
    BITWISE_XOR("^"),
    EQUAL("=="),
    NOT_EQUAL("!="),
    LESS("<"),
    LESS_EQUAL("<="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    ASSIGN("="),
    // Punctuation
    LEFT_PAREN("("), RIGHT_PAREN(")"),
    LEFT_BRACE("{"), RIGHT_BRACE("}"),
    SEMICOLON(";"),
    COMMA(","),
    // Misc
    EOF(null),
    ERROR(null);

    private final String lexeme;

    TokenKind(String lexeme) {
        this.lexeme = lexeme;
    }

    public String lexeme() {
        return lexeme;
    }

    public boolean isTypeKind() {
        return switch (this) {
            case VOID, DOUBLE, BOOL, BYTE, UBYTE, SHORT, USHORT, INT, UINT, LONG, ULONG, FLOAT -> true;
            default -> false;
        };
    }

    public static TokenKind getKeywordOrIdentifier(String lexeme) {
        return switch (lexeme) {
            case "void"   -> VOID;
            case "bool"   -> BOOL;
            case "byte"   -> BYTE;
            case "ubyte"  -> UBYTE;
            case "short"  -> SHORT;
            case "ushort" -> USHORT;
            case "int"    -> INT;
            case "uint"   -> UINT;
            case "long"   -> LONG;
            case "ulong"  -> ULONG;
            case "float"  -> FLOAT;
            case "double" -> DOUBLE;
            case "if"     -> IF;
            case "else"   -> ELSE;
            case "do"     -> DO;
            case "while"  -> WHILE;
            case "for"    -> FOR;
            case "return" -> RETURN;
            case "true"   -> TRUE;
            case "false"  -> FALSE;
            default       -> IDENTIFIER;
        };
    }

    public static TokenKind getOperatorOrPunctuation(char first, char second) {
        return switch (first) {
            case '+' -> ADD;
            case '-' -> SUB;
            case '*' -> MULT;
            case '/' -> DIV;
            case '%' -> MOD;
            case '~' -> BITWISE_NOT;
            case '^' -> BITWISE_XOR;
            case '(' -> LEFT_PAREN;
            case ')' -> RIGHT_PAREN;
            case '{' -> LEFT_BRACE;
            case '}' -> RIGHT_BRACE;
            case ';' -> SEMICOLON;
            case ',' -> COMMA;
            case '!' -> second == '=' ? NOT_EQUAL : LOGICAL_NOT;
            case '&' -> second == '&' ? LOGICAL_AND : BITWISE_AND;
            case '|' -> second == '|' ? LOGICAL_OR : BITWISE_OR;
            case '=' -> second == '=' ? EQUAL : ASSIGN;
            case '<' -> second == '=' ? LESS_EQUAL : LESS;
            case '>' -> second == '=' ? GREATER_EQUAL : GREATER;
            default  -> ERROR;
        };
    }
}