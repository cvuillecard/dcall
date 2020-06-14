package com.dcall.core.configuration.utils.parser;

public final class ASCII {
    // alpha
    private static final int A = 65; // 'A'
    private static final int Z = 90; // 'Z'
    private static final int a = 97; // 'a'
    private static final int z = 122; // 'z'
    // num
    private static final int zero = 48; // '0'
    private static final int nine = 57; // '9'
    // backslash
    private static final int backspace = 8; // '\b'
    private static final int horizontal_tab = 9; // '\t'
    private static final int new_line = 10; // '\n'
    private static final int carriage_return = 13; // '\r'
    private static final int NUL = 0; // '\0'
    // special
    private static final int space = 32;
    private static final int openParenthesis = 40;
    private static final int closeParenthesis = 41;
    // operator
    private static final int mul = 42;
    private static final int plus = 43;
    private static final int minus = 45;
    private static final int div = 47;
    private static final int and = 38;
    private static final int or = 124;

    // detect char type methods
    public static boolean isSpace(final char c) { return c == space; }
    public static boolean isOpenParenthesis(final char c) { return c == openParenthesis; }
    public static boolean isCloseParenthesis(final char c) { return c == closeParenthesis; }
    public static boolean isParenthesis(final char c) { return isOpenParenthesis(c) || isCloseParenthesis(c); }
    public static boolean isMul(final char c) { return c == mul; }
    public static boolean isPlus(final char c) { return c == plus; }
    public static boolean isMinus(final char c) { return c == minus; }
    public static boolean isDiv(final char c) { return c == div; }
    public static boolean isAnd(final char c) { return c == and; }
    public static boolean isOr(final char c) { return c == or; }
    public static boolean isUnaryOperator(final char c) { return isAnd(c) || isOr(c); }
    public static boolean isArithmeticOperator(final char c) { return isMul(c) || isPlus(c) || isMinus(c) || isDiv(c); }
    public static boolean isOperator(final char c) { return isUnaryOperator(c) || isArithmeticOperator(c); }
    public static boolean isTab(final char c) { return c == horizontal_tab; }
    public static boolean isNewLine(final char c) { return c == new_line || c == carriage_return; }
    public static boolean isBlank(final char c) { return isSpace(c) || isTab(c); }
    public static boolean isAlpha(final char c) { return (c >= A && c <= Z) || (c >= a && c <= z); }
    public static boolean isNum(final char c) { return c >= zero && c <= nine; }
    public static boolean isAlphaNum(final char c) { return isAlpha(c) || isNum(c); }
    public static boolean isBackslash(final char c) { return (c >= backspace && c <= carriage_return) || c == NUL; }
}
