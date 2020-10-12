package com.dcall.core.configuration.generic.parser.expression.token.impl;

import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.expression.token.TokenSolver;

import java.util.function.Predicate;

public final class BuiltInTokenSolver implements TokenSolver {
    @Override public Predicate<Character> isBlank() { return c -> ASCII.isBlank(c); }
    @Override public Predicate<Character> isNotToken() { return c -> !ASCII.isBitwise(c) && !ASCII.isParenthesis(c); }
    @Override public Predicate<Character> isOpenToken() { return c -> ASCII.isOpenParenthesis(c); }
    @Override public Predicate<Character> isCloseToken() {
        return c -> ASCII.isCloseParenthesis(c);
    }
    @Override public Predicate<Character> isOperator() { return c -> ASCII.isBitwise(c); }
}
