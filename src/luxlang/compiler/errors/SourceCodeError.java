package luxlang.compiler.errors;

public interface SourceCodeError {
    String MESSAGE_TEMPLATE = """
        Error: %s
        At line: %d, column: %d
        """;

    String reason();
    int line();
    int column();

    default String message() {
        return MESSAGE_TEMPLATE.formatted(reason(), line(), column());
    }
}