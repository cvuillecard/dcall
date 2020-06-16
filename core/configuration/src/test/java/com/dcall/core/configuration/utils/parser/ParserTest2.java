package com.dcall.core.configuration.utils.parser;

import com.dcall.core.configuration.utils.parser.expression.Expression;
import com.dcall.core.configuration.utils.tree.BTree;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserTest2 {
    private static final Logger LOG = LoggerFactory.getLogger(ParserTest2.class);

    private static final Parser2 parser = new Parser2();

//    public CharSequence evalTree(final BTree<CharSequence> tree) {
//        BTree<CharSequence> ptr = tree;
//        BTree<CharSequence> last = null;
//        BTree<CharSequence> visited = null;
//        CharSequence res = "";
//
//        while (ptr != null && ptr != visited) {
//            if (ptr.getRight() != null && ptr.getRight() != last) {
//                res = res.toString() + evalTree(ptr.getRight().firstLeft());
//                visited = ptr.getParent();
//            }
//            else {
//                last = ptr;
//                LOG.debug(last.getData().toString());
//                res = last.getData().toString() + res.toString();
//            }
//            ptr = ptr.getParent();
//        }
////        LOG.debug(res.toString());
//
//        return res;
//    }

    @Test
    public void should_parse_string_as_expression_btree_parse() {
        final String exp = "((1 + 3 - (4 + 5)) - 6) / 7";

        BTree<Expression> root = parser.reset().parse(exp, 0, exp.length());

        Assert.assertTrue(root != null);
    }

}
