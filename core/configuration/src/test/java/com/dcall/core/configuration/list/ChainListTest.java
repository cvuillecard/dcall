package com.dcall.core.configuration.list;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainListTest {
    private static final Logger LOG = LoggerFactory.getLogger(ChainListTest.class);
    private com.dcall.core.configuration.list.List list;
    private com.dcall.core.configuration.list.List head;
    private com.dcall.core.configuration.list.List tail;

    @Before
    public void init() {
        list = new ChainList(new String[] {"a", "b", "c", "d", "e", "f", "g", "h"});
        head = list.getHead();
        tail = list.getTail();
    }

    @Test
    public void should_count_list_count() {
        Assert.assertEquals(8, list.count());
    }

}
