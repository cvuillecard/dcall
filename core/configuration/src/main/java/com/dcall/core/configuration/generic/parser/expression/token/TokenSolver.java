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
     * method testing what is considered as blank at idx in the sequence.
     *
     * Note : this method comes with the same predicate method to extend its behaviour.
     *
     * @param seq to iterate
     * @param idx of the current character in the sequence
     * @return true or false if the character at idx is considered as blank
     */
    boolean isBlank(final CharSequence seq, final int idx);

    /**
     * condition defining which character(s) is/are not a token
     * @return condition
     */
    Predicate<Character> isNotToken();

    /**
     * method testing what is considered as a token at idx in the sequence.
     *
     * Note : this method comes with the same predicate method to extend its behaviour.
     *
     * @param seq to iterate
     * @param idx of the current character in the sequence
     * @return true or false if the character at idx is considered as a token
     */
    boolean isNotToken(final CharSequence seq, final int idx);

    /**
     * condition defining which character(s) is/are open token(s) group
     * @return condition
     */
    Predicate<Character> isOpenToken();

    /**
     * method testing what is considered as a open token(s) group at idx in the sequence.
     *
     * Note : this method comes with the same predicate method to extend its behaviour.
     *
     * @param seq to iterate
     * @param idx of the current character in the sequence
     * @return true or false if the character at idx is considered as a open token(s) group
     */
    boolean isOpenToken(final CharSequence seq, final int idx);

    /**
     * condition defining which character(s) is/are close token(s) group
     * @return condition
     */
    Predicate<Character> isCloseToken();

    /**
     * method testing what is considered as a close token(s) group at idx in the sequence.
     *
     * Note : this method comes with the same predicate method to extend its behaviour.
     *
     * @param seq to iterate
     * @param idx of the current character in the sequence
     * @return true or false if the character at idx is considered as a close token(s) group
     */
    boolean isCloseToken(final CharSequence seq, final int idx);

    /**
     * condition defining which character(s) is/are operator(s)
     * @return condition
     */
    Predicate<Character> isOperator();

    /**
     * method testing what is considered as an operator at idx in the sequence.
     *
     * Note : this method comes with the same predicate method to extend its behaviour.
     *
     * @param seq to iterate
     * @param idx of the current character in the sequence
     * @return true or false if the character at idx is considered as an operator
     */
    boolean isOperator(final CharSequence seq, final int idx);

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
