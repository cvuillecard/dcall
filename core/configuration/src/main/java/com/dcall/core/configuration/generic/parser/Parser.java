package com.dcall.core.configuration.generic.parser;

import com.dcall.core.configuration.generic.parser.expression.operator.solver.impl.ArithmeticOperatorSolver;
import com.dcall.core.configuration.generic.parser.expression.token.impl.ArithmeticTokenSolver;
import com.dcall.core.configuration.generic.parser.expression.token.TokenSolver;
import com.dcall.core.configuration.utils.StringUtils;
import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.impl.StringOperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operator.Operator;
import com.dcall.core.configuration.generic.parser.expression.operator.OperatorType;
import com.dcall.core.configuration.generic.parser.expression.operator.solver.OperatorSolver;
import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Simple scanner less parser which parse a char sequence and produces a binary tree of expressions parsed.
 *  The operator, token and operand definitions are used to identify what is an expression.
 *
 *  Important : This implementation needs an operator token between each expressions to construct a valid expression's tree.
 *
 *  If the sequence is constituted by one string considered as operand, the tree will only have one element.
 *
 *  The parser will always try to return a valid tree with the input char sequence except if sequence syntax is malformed
 *  (according the token/operator/operand implementations. See the classes linked below)
 *
 *  If no implementations are given for token/operator/operand solvers, then the default implementation will be used as defined in default constructor 'parser()'
 *
 * @see com.dcall.core.configuration.generic.parser.expression.Expression
 * @see com.dcall.core.configuration.generic.parser.expression.operator.Operator
 * @see com.dcall.core.configuration.generic.parser.expression.operand.Operand
 *
 * @see com.dcall.core.configuration.generic.parser.expression.token.TokenSolver
 * @see com.dcall.core.configuration.generic.parser.expression.operator.solver.OperatorSolver
 * @see com.dcall.core.configuration.generic.parser.expression.operand.solver.OperandSolver
 */
public final class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
    private BTree<Expression> first;
    private TokenSolver tokenSolver;
    private OperatorSolver operatorSolver;
    private OperandSolver operandSolver;

    public Parser() {
        this.tokenSolver = new ArithmeticTokenSolver();
        this.operatorSolver = new ArithmeticOperatorSolver();
        this.operandSolver = new StringOperandSolver();
    }

    public Parser(final OperatorSolver operatorSolver, final OperandSolver operandSolver) {
        this.tokenSolver = new ArithmeticTokenSolver();
        this.operatorSolver = operatorSolver;
        this.operandSolver = operandSolver;
    }

    public Parser(final TokenSolver tokenSolver, final OperatorSolver operatorSolver, final OperandSolver operandSolver) {
        this.tokenSolver = tokenSolver;
        this.operatorSolver = operatorSolver;
        this.operandSolver = operandSolver;
    }

    /**
     * Check the operator type and set it as value on operator class.
     *
     * Actually : an operator composed of tow unary operators is considered as LOGICAL,
     * and an unary operator as ARITHMETIC except for '&' or '|' operators considered as BITWISE
     *
     * Note : this behaviour is going to change
     *
     * @param operator
     * @return
     */
    private OperatorType unaryOperatorType(final Operator operator) {
        return ASCII.isBitwise(operator.getValue().charAt(0)) ? OperatorType.BITWISE : OperatorType.ARITHMETIC;
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
    public int parseOperator(final BTree<Expression> node, final CharSequence seq, final int idx, final int endIdx) {
        final int nextIdx = idx + 1;
        final Operator operator = new Operator().setSolver(operatorSolver::solve);

        if (nextIdx < endIdx && ASCII.isLogicalOperator(seq.charAt(idx), seq.charAt(nextIdx))) {
            node.setData(operator.setValue(seq.subSequence(idx, nextIdx + 1)).setOperatorType(OperatorType.LOGICAL));
            return 2;
        }
        else {
            node.setData(operator.setValue(seq.subSequence(idx, idx + 1)).setOperatorType(unaryOperatorType(operator)));
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
    private BTree<Expression> updateRef(BTree<Expression> ref, final BTree<Expression> operator, final BTree<Expression> node) {
        if (operator != null) {
            ((Operator)operator.getData()).setRight(node.getData());
            operator.setRight(node);
            ref = operator;
        } else
            ref = node;

        if (first == null)
            first = ref;

        return ref;
    }


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
    private int iterTokenGroup(final CharSequence seq, int idx, final int endIdx) {
        int nOpen = 1;
        int nClose = 0;

        while (idx < endIdx && nOpen != nClose) {
            if (tokenSolver.isOpenToken().test(seq.charAt(idx)))
                nOpen++;
            else if (tokenSolver.isCloseToken().test(seq.charAt(idx)))
                nClose++;
            idx++;
        }

        if (nOpen != nClose)
            throw new IllegalArgumentException(
                    "Bad syntax : Missing open or close token in expression > "+ seq.toString()
            );

        return idx - 1;
    }

    /**
     * Parse a char sequence in specified bounds and produces a binary tree of expressions
     *
     * @param seq char sequence to parse (String or char[])
     * @param idx the starting idx [inclusive]
     * @param endIdx the end idx [exclusive]
     * @return BTree - the BTree of expressions constructed
     */
    public BTree<Expression> parse(final CharSequence seq, int idx, final int endIdx) {
        if (seq == null || seq.length() == 0) return null;
        BTree<Expression> ptr = null;

        try {
            while ((idx = IterStringUtils.iterFront(seq, idx, endIdx, c -> tokenSolver.isBlank().test(c) || tokenSolver.isCloseToken().test(c))) < endIdx) {
                BTree<Expression> operator = null;

                if (tokenSolver.isOperator().test(seq.charAt(idx))) {
                    idx += parseOperator(operator = new Node<>(), seq, idx, endIdx);
                    idx = IterStringUtils.iterFront(seq, idx, endIdx, tokenSolver.isBlank());
                    if (ptr != null) {
                        operator.setLeft(ptr);
                        ((Operator)operator.getData()).setLeft(ptr.getData());
                    }
                }

                if (tokenSolver.isOpenToken().test(seq.charAt(idx))) {
                    idx++;
                    int endTokenIdx = iterTokenGroup(seq, idx, endIdx);
                    if (endTokenIdx  > idx) {
                        ptr = updateRef(ptr, operator, parse(seq, idx, endTokenIdx));
                        idx = endTokenIdx;
                    }
                }
                else {
                    int end = IterStringUtils.iterFront(seq, idx, endIdx, tokenSolver.isNotToken());
                    if (end > idx) {
                        ptr = updateRef(ptr, operator, new Node(parseOperand(seq, idx, end)));
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

    private Operand parseOperand(CharSequence seq, int idx, int end) {
        return new Operand(operandSolver.solve(StringUtils.trim(seq.subSequence(idx, end))));
    }

    // utils
    public BTree<Expression> getFirst() { return first; }
    public Parser reset() { this.first = null; return this; }

    // getter
    public TokenSolver getTokenSolver() { return tokenSolver; }
    public OperatorSolver getOperatorSolver() { return operatorSolver; }
    public OperandSolver getOperandSolver() { return operandSolver; }

    // setter
    public Parser setTokenSolver(final TokenSolver tokenSolver) { this.tokenSolver = tokenSolver; return this; }
    public Parser setOperatorSolver(OperatorSolver operatorSolver) { this.operatorSolver = operatorSolver; return this; }
    public Parser setOperandSolver(final OperandSolver operandSolver) { this.operandSolver = operandSolver; return this; }
}
