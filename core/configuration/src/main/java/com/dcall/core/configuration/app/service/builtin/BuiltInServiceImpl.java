package com.dcall.core.configuration.app.service.builtin;

import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.generic.parser.expression.EvalExp;
import com.dcall.core.configuration.generic.parser.expression.Expression;
import com.dcall.core.configuration.generic.parser.expression.operand.Operand;
import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import com.dcall.core.configuration.utils.tree.BTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BuiltInServiceImpl extends AbstractCommand implements BuiltInService {
    private static final Logger LOG = LoggerFactory.getLogger(BuiltInServiceImpl.class);
    private Parser parser = new Parser();

    @Override
    public byte[] execute(final String... input) {
        final String line = input[0];

        parser.reset().parse(line, 0, line.length());
        final BTree<Expression> node = parser.getFirst();

        if (node.getParent() != null)
            return (byte[])((Operand)EvalExp.eval(node, node.getParent())).getValue();
        else
            return parser.getOperatorSolver().execute((Operand) node.getData());
    }

    @Override
    public byte[] execute() {
        return null;
    }

    @Override
    public Parser getParser() { return this.parser; }

    @Override
    public BuiltInService setParser(final Parser parser) { this.parser = parser; return this; }
}
