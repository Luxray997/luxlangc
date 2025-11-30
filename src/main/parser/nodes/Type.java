package main.parser.nodes;

public enum Type {
    VOID,
    BOOL,
    BYTE,
    UBYTE,
    SHORT,
    USHORT,
    INT,
    UINT,
    LONG,
    ULONG,
    FLOAT,
    DOUBLE;

    public boolean isIntegerType() {
        return switch (this) {
            case BYTE, UBYTE, SHORT, USHORT, INT, UINT, LONG, ULONG -> true;
            default -> false;
        };
    }

    public boolean isFloatingPointType() {
        return switch (this) {
            case FLOAT, DOUBLE -> true;
            default -> false;
        };
    }

    public boolean isSignedNumberType() {
        return switch (this) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> true;
            default -> false;
        };
    }

    public boolean isNumberType() {
        return switch (this) {
            case BYTE, UBYTE, SHORT, USHORT, INT, UINT, LONG, ULONG, FLOAT, DOUBLE -> true;
            default -> false;
        };
    }
}