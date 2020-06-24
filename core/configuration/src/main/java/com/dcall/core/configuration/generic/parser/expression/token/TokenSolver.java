package com.dcall.core.configuration.generic.parser.expression.token;

import java.util.function.Predicate;

/**
 * Definition of what is a token
 *
 * @see com.dcall.core.configuration.generic.parser.Parser
 */
public interface TokenSolver {
    /**
     * condition defining which character(s) is/are a blank character
     * @return condition
     */
    Predicate<Character> isBlank();

    /**
     * condition defining which character(s) is/are not a token
     * @return condition
     */
    Predicate<Character> isNotToken();

    /**
     * condition defining which character(s) is/are open token(s) group
     * @return condition
     */
    Predicate<Character> isOpenToken();

    /**
     * condition defining which character(s) is/are close token(s) group
     * @return condition
     */
    Predicate<Character> isCloseToken();

    /**
     * condition defining which character(s) is/are operator(s)
     * @return condition
     */
    Predicate<Character> isOperator();

    /**
     * Iterate the sequence and returns the last closing group token index according to the given token opening group index in parameter (idx).
     *
     * Note : the condition behaviour defining which characters are open group tokens and close group tokens is defined by
     * isOpenToken() and isCloseToken() methods of the TokenSolver implementation.
     *
     * @param seq
     * @param idx
     * @param endIdx
     * @return the index of the last closing token in bounds defined by idx as open index, and endIdx the last iterative index
     */
    int iterTokenGroup(final CharSequence seq, int idx, final int endIdx);

}
