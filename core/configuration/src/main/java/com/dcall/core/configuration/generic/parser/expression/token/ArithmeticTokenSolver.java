package com.dcall.core.configuration.generic.parser.expression.token;

import com.dcall.core.configuration.generic.parser.ASCII;

import java.util.function.Predicate;

public class ArithmeticTokenSolver implements TokenSolver {
    @Override
    public Predicate<Character> isBlank() { return c -> ASCII.isBlank(c); }

    @Override
    public boolean isBlank(final CharSequence seq, final int idx) {  return isBlank().test(seq.charAt(idx)); }

    @Override
    public Predicate<Character> isNotToken() { return c -> !ASCII.isOperator(c) && !ASCII.isParenthesis(c); }

    @Override
    public boolean isNotToken(final CharSequence seq, final int idx) { return isNotToken().test(seq.charAt(idx)); }

    @Override
    public Predicate<Character> isOpenToken() { return c -> ASCII.isOpenParenthesis(c); }

    @Override
    public boolean isOpenToken(final CharSequence seq, final int idx) { return isOpenToken().test(seq.charAt(idx)); }

    @Override
    public Predicate<Character> isCloseToken() { return c -> ASCII.isCloseParenthesis(c); }

    @Override
    public boolean isCloseToken(final CharSequence seq, final int idx) { return isCloseToken().test(seq.charAt(idx)); }

    @Override
    public Predicate<Character> isOperator() { return c -> ASCII.isOperator(c); }

    @Override
    public boolean isOperator(final CharSequence seq, final int idx) { return isOperator().test(seq.charAt(idx)); }

    @Override
    public int iterTokenGroup(final CharSequence seq, int idx, final int endIdx) {
        int nOpen = 1;
        int nClose = 0;

        while (idx < endIdx && nOpen != nClose) {
            if (isOpenToken().test(seq.charAt(idx)))
                nOpen++;
            else if (isCloseToken().test(seq.charAt(idx)))
                nClose++;
            idx++;
        }

        if (nOpen != nClose)
            throw new IllegalArgumentException(
                    "Bad syntax : Missing open or close token in expression > "+ seq.toString()
            );

        return idx - 1;
    }
}
