package com.dcall.core.configuration.app.service.builtin;

import com.dcall.core.configuration.app.constant.BuiltInAction;
import com.dcall.core.configuration.generic.parser.ASCII;
import com.dcall.core.configuration.generic.parser.IterStringUtils;
import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.generic.parser.expression.EvalExp;
import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.parser.expression.token.impl.ArithmeticTokenSolver;
import com.dcall.core.configuration.generic.parser.expression.token.impl.BuiltInTokenSolver;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuiltInServiceImpl extends AbstractCommand implements BuiltInService {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInServiceImpl.class);
    private Parser parser = new Parser();
    private BTree<Expression> expr = null;

    @Override
    public byte[] execute(final String... input) {
        final String line = input[0];

        configureParser(line).parse(line, 0, line.length());

        this.expr = parser.getFirst();

        return this.execute();
    }

    private Parser configureParser(final String line) {
        try {
            final int nextIdx = IterStringUtils.iterFront(line, 0, c -> !ASCII.isBlank(c));
            final String cmd = line.substring(0, nextIdx);

            parser.setTokenSolver(BuiltInAction.valueOf(cmd) != null ? new BuiltInTokenSolver() : new ArithmeticTokenSolver());
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return parser.reset();
    }

    @Override
    public byte[] execute() {
        try {
            if (expr.getParent() != null) {
                final Operand exp = ((Operand) EvalExp.eval(expr, expr.getParent()));
                byte[] res = exp.getValue() instanceof byte[] ? (byte[]) exp.getValue() : exp.toString().getBytes();
                return res;
            } else
                return parser.getOperatorSolver().execute((Operand) expr.getData());
        }
        catch (StackOverflowError e){
            LOG.debug(e.toString());
            return e.toString().getBytes();
        }
    }

    @Override
    public Parser getParser() { return this.parser; }

    @Override
    public BuiltInService setParser(final Parser parser) { this.parser = parser; return this; }
}
