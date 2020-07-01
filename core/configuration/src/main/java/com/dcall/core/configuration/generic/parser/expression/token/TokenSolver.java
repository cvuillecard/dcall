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
}
