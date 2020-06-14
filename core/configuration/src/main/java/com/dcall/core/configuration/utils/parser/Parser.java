package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

/**
 *  Simple scannerless parser : simple parsing based on ASCII class for operators and token definition.
 */
public final class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private BTree first;

    /**
     * Condition defining when a char is not considered as token
     *
     * @return condition
     */
    private Predicate<Character> isNotToken() {
        return c -> !ASCII.isOperator(c) && !ASCII.isParenthesis(c);
    }

    /**
     * Parses the char sequence and count the number of open tokens and close tokens given in parameters.
     *
     * if the number of openToken is not equal to the number of closeToken then an exception is thrown else idx of last closeToken is returned.
     *
     * @param seq char sequence iterated
     * @param idx start index of the defined 'openToken' in char sequence [inclusive]
     * @param endIdx iteration's end index [exclusive]
     * @param openToken the int value of the token char opening
     * @param closeToken the int value of the token char closing
     * @return index of last closing token in char sequence
     */
    public int parseTokenGroup(final CharSequence seq, int idx, final int endIdx, final int openToken, final int closeToken) {
        int nOpen = 1;
        int nClose = 0;

        while (idx < endIdx && nOpen != nClose) {
            if (openToken == seq.charAt(idx))
                nOpen++;
            else if (closeToken == seq.charAt(idx))
                nClose++;
            idx++;
        }

        if (nOpen != nClose)
            throw new IllegalArgumentException(
                    "Bad syntax : Missing open('"+ (char) openToken + "') or close('"+ (char) closeToken +"') token in expression > "+ seq.toString()
            );

        return idx - 1;
    }

    /**
     * Parses the char sequence of the operator at idx and update node's datas with the operator.
     *
     * if the same char exists after idx (at idx + 1) then the operator is considered bitwise
     * else the unary operator at idx is considered valid whatever its value.
     *
     * Note : can be refactored, because in one case the binary operator is handled by ASCII class definition,
     * and in the else scope, we don't check its value, so it might corrupt the node value updated (depending of needs).
     *
     * @param node node to be updated with operator value
     * @param seq char sequence iterated
     * @param idx index of the defined 'operator' in char sequence [inclusive]
     * @param endIdx iteration's end index [exclusive]
     * @return the padding of operator parsed in char sequence
     */
    public int parseOperator(final BTree<CharSequence> node, final CharSequence seq, final int idx, final int endIdx) {
        final int nextIdx = idx + 1;

        if (nextIdx < endIdx && ASCII.isBinaryOperator(seq.charAt(idx), seq.charAt(nextIdx))) {
            node.setData(seq.subSequence(idx, nextIdx + 1).toString());
            return 2;
        }
        else {
            node.setData(seq.subSequence(idx, idx + 1));
            return 1;
        }
    }

    /**
     * Update the pointer's reference 'ref' with 'operator' reference or 'node' reference.
     *
     * if 'operator' reference is not null then 'node' reference is assigned to its right child and 'operator' reference is assigned to 'ref',
     * else 'node' reference is assigned to 'ref' reference
     *
     * @param ref the pointer referenced
     * @param operator the operator pointer reference
     * @param node the node pointer reference
     * @return 'ref' : the pointer updated
     */
    private BTree<CharSequence> updateRef(BTree<CharSequence> ref, final BTree<CharSequence> operator, final BTree<CharSequence> node) {
        if (operator != null) {
            operator.setRight(node);
            ref = operator;
        } else
            ref = node;

        if (first == null)
            first = ref;

        return ref;
    }

    /**
     * Parse a char sequence in specified bounds and produces a binary tree of expressions
     *
     * @param seq char sequence to parse (String or char[])
     * @param idx the starting idx [inclusive]
     * @param endIdx the end idx [exclusive]
     * @return BTree - the BTree of expressions constructed
     */
    public BTree<CharSequence> parse(final CharSequence seq, int idx, final int endIdx) {
        if (seq == null || seq.length() == 0) return null;
        BTree<CharSequence> ptr = null;

        try {
            while ((idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> ASCII.isBlank(c) || ASCII.isCloseParenthesis(c))) < endIdx) {
                BTree<CharSequence> operator = null;

                if (ASCII.isOperator(seq.charAt(idx))) {
                    idx += parseOperator(operator = new Node<>(), seq, idx, endIdx);
                    idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> ASCII.isBlank(c));
                    if (ptr != null)
                        operator.setLeft(ptr);
                }

                if (ASCII.isOpenParenthesis(seq.charAt(idx))) {
                    idx++;
                    int endTokenIdx = parseTokenGroup(seq, idx, endIdx, ASCII.openParenthesis, ASCII.closeParenthesis);
                    if (endTokenIdx  > idx) {
                        ptr = updateRef(ptr, operator, parse(seq, idx, endTokenIdx));
                        idx = endTokenIdx;
                    }
                }
                else {
                    int end = IterStringUtils.iterFront(seq, idx, endIdx, isNotToken());
                    if (end > idx) {
                        ptr = updateRef(ptr, operator, new Node(seq.subSequence(idx, end)));
                        idx = end;
                    }
                }
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return ptr;
    }

    public BTree getFirst() { return first; }

    Parser reset() { this.first = null; return this; }
}
