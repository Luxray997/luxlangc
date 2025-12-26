package luxlang.compiler.errors;

public interface SourceCodeWarning {
    String MESSAGE_TEMPLATE = """
        Warning: %s
        At line: %d, column: %d
        """;

    String reason();
    int line();
    int column();

    default String message() {
        return MESSAGE_TEMPLATE.formatted(reason(), line(), column());
    }
}