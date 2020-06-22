package com.dcall.core.configuration.generic.parser.expression.token;

import com.dcall.core.configuration.generic.parser.ASCII;

import java.util.function.Predicate;

public class DefaultTokenSolver implements TokenSolver {
    @Override
    public Predicate<Character> isBlank() {
        return c -> ASCII.isBlank(c);
    }

    @Override
    public Predicate<Character> isNotToken() {
        return c -> !ASCII.isOperator(c) && !ASCII.isParenthesis(c);
    }

    @Override
    public Predicate<Character> isOpenToken() {
        return c -> ASCII.isOpenParenthesis(c);
    }

    @Override
    public Predicate<Character> isCloseToken() {
        return c -> ASCII.isCloseParenthesis(c);
    }

    @Override
    public Predicate<Character> isOperator() {
        return c -> ASCII.isOperator(c);
    }

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
