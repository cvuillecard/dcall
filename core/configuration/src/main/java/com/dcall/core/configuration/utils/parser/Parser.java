package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

/**
 *  Simple scannerless
 */
public final class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    private Predicate<Character> isNotToken() {
        return c -> !ASCII.isOperator(c) && !ASCII.isParenthesis(c);
    }

    public int endParenthesisIdx(final CharSequence seq, int idx) {
        int openParenthesis = 1;
        int closeParenthesis = 0;

        while (idx < seq.length() && openParenthesis != closeParenthesis) {
            if (ASCII.isOpenParenthesis(seq.charAt(idx)))
                openParenthesis++;
            else if (ASCII.isCloseParenthesis(seq.charAt(idx)))
                closeParenthesis++;
            idx++;
        }

        if (openParenthesis != closeParenthesis)
            throw new IllegalArgumentException("Bad syntax : Missing parenthesis in expression : " + seq.toString());

        return idx - 1;
    }

    public BTree<CharSequence> parse(final CharSequence seq) {
        if (seq == null || seq.length() == 0) return null;
        BTree<CharSequence> ptr = null;

        try {
            int idx = 0;
            while ((idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c) || ASCII.isCloseParenthesis(c))) < seq.length()) {
                BTree<CharSequence> operator = null;
                if (ASCII.isOperator(seq.charAt(idx))) {
                    operator = new Node<>(seq.subSequence(idx, idx + 1));
                    if (ptr != null && operator.getLeft() == null)
                        operator.setLeft(ptr);
                    idx++;
                    idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c));
                }
                if (ASCII.isOpenParenthesis(seq.charAt(idx))) {
                    idx++;
                    int end = endParenthesisIdx(seq, idx);
                    if (end  > idx) {
                        final BTree<CharSequence> exp = parse(seq.subSequence(idx, end));
                        if (operator != null) {
                            operator.setRight(exp);
                            ptr = operator;
                        } else
                            ptr = exp;
                        idx = end;
                    }
                }
                else {
                    int end = IterStringUtils.iter(seq, idx, isNotToken());
                    if (end > idx) {
                        final Node exp = new Node(seq.subSequence(idx, end));
                        if (operator != null) {
                            operator.setRight(exp);
                            ptr = operator;
                        } else
                            ptr = exp;
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

}
