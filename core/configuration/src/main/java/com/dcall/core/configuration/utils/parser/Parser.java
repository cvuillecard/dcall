package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 *  Simple scannerless
 */
public final class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    private Predicate<Character> isNotToken() {
        return c -> !ASCII.isOperator(c) && !ASCII.isParenthesis(c);
    }

    public BTree<CharSequence> parse(final CharSequence seq) {
        if (seq == null || seq.length() == 0) return null;
        BTree<CharSequence> ptr = null;
        BTree<CharSequence> first = null;

        try {
            int idx = 0;
            while ((idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c) || ASCII.isParenthesis(c))) < seq.length()) {
                BTree<CharSequence> operator = null;
                if (ASCII.isOperator(seq.charAt(idx))) {
                    operator = new Node<>(seq.subSequence(idx, idx + 1));
                    if (ptr != null && operator.getLeft() == null)
                        operator.setLeft(ptr);
                    idx++;
                    idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c) || ASCII.isParenthesis(c));
                }
                int end = IterStringUtils.iter(seq, idx, isNotToken());
                if (end > idx) {
                    final Node exp = new Node(seq.subSequence(idx, end));
                    if (operator != null) {
                        operator.setRight(exp);
                        ptr = operator;
                    }
                    else
                        ptr = exp;
                    if (first == null)
                        first = ptr;
                    idx = end;
                }
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return first;
    }


    public List<Node<CharSequence>> parseExpression(final CharSequence seq) {
        if (seq == null || seq.length() == 0) return null;
        final List<Node<CharSequence>> list = new ArrayList<>();

        try {
            int idx = 0;
            while ((idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c) || ASCII.isParenthesis(c))) < seq.length()) {
                if (ASCII.isOperator(seq.charAt(idx))) {
                    list.add(new Node<>(seq.subSequence(idx, idx + 1)));
                    idx++;
                    idx = IterStringUtils.iter(seq, idx, c -> ASCII.isBlank(c) || ASCII.isParenthesis(c));
                }
                int end = IterStringUtils.iter(seq, idx, isNotToken());
                if (end > idx) {
                    list.add(new Node(seq.subSequence(idx, end)));
                    idx = end;
                } else
                    throw new IllegalArgumentException("An expression should start after '(' or not with an alphanumeric operand");

            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }

        return list.isEmpty() ? null : list;
    }
}
