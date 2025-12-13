package main.parser.nodes;

public enum Type {
    VOID(0),
    BOOL(1),
    BYTE(1),
    UBYTE(1),
    SHORT(2),
    USHORT(2),
    INT(4),
    UINT(4),
    LONG(8),
    ULONG(8),
    FLOAT(4),
    DOUBLE(8);

    Type(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    private final int sizeBytes;

    public String lexeme() {
        return this.toString().toLowerCase();
    }

    public int sizeBytes() {
        return sizeBytes;
    }

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