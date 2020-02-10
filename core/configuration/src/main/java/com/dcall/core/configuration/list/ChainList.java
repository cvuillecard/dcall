package com.dcall.core.configuration.list;

public class ChainList extends AbstractList {
    public ChainList() { super(); }
    public <T> ChainList(final T data) { super(data); }

    public <T> ChainList(final T... data) {
        super(data);
        if (data != null && data.length > 0) {
            AbstractList tail = this;
            for (int i = 1; i < data.length; i++) {
                final AbstractList elem = new ChainList(data[i]);
                tail.insertAfter(elem);
                tail = elem;
            }
        }
    }
}
