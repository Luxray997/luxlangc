package main.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberLimits {
    public static final BigInteger ULONG_MAX_VALUE = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    public static final BigInteger ULONG_MIN_VALUE = BigInteger.ZERO;

    public static final BigInteger LONG_MAX_VALUE = new BigInteger(String.valueOf(Long.MAX_VALUE));
    public static final BigInteger LONG_MIN_VALUE = new BigInteger(String.valueOf(Long.MIN_VALUE));

    public static final BigInteger UINT_MAX_VALUE = new BigInteger("FFFFFFFF", 16);
    public static final BigInteger UINT_MIN_VALUE = BigInteger.ZERO;

    public static final BigInteger INT_MAX_VALUE = new BigInteger(String.valueOf(Integer.MAX_VALUE));
    public static final BigInteger INT_MIN_VALUE = new BigInteger(String.valueOf(Integer.MIN_VALUE));

    public static final BigInteger USHORT_MAX_VALUE = new BigInteger("FFFF", 16);
    public static final BigInteger USHORT_MIN_VALUE = BigInteger.ZERO;

    public static final BigInteger SHORT_MAX_VALUE = new BigInteger(String.valueOf(Short.MAX_VALUE));
    public static final BigInteger SHORT_MIN_VALUE = new BigInteger(String.valueOf(Short.MIN_VALUE));

    public static final BigInteger UBYTE_MAX_VALUE = new BigInteger("FF", 16);
    public static final BigInteger UBYTE_MIN_VALUE = BigInteger.ZERO;

    public static final BigInteger BYTE_MAX_VALUE = new BigInteger(String.valueOf(Short.MAX_VALUE));
    public static final BigInteger BYTE_MIN_VALUE = new BigInteger(String.valueOf(Short.MIN_VALUE));

    public static final BigDecimal DOUBLE_MAX_VALUE = new BigDecimal(Double.MAX_VALUE);
    public static final BigDecimal DOUBLE_MIN_VALUE = new BigDecimal(Double.MIN_VALUE);

    public static final BigDecimal FLOAT_MAX_VALUE = new BigDecimal(Float.MAX_VALUE);
    public static final BigDecimal FLOAT_MIN_VALUE = new BigDecimal(Float.MIN_VALUE);
}
