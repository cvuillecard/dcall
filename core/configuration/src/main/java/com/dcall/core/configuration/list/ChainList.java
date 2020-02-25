package com.dcall.core.configuration.list;

public class ChainList extends AbstractList {
    private List head = this;
    private List tail = this;
    private int size = 0;

    public ChainList() {
        super();
        this.setSize( 1);
    }

    public <T> ChainList(final T data) {
        super(data);
        this.setSize( 1);
    }

    public <T> ChainList(final T... data) {
        super(data);
        if (data != null && data.length > 0) {
            for (int i = 1; i < data.length; i++) {
                final ChainList elem = new ChainList(data[i]);
                this.size += 1;
                elem.setSize(this.size);
                elem.setHead(this);
                this.tail.insertAfter(elem);
                tail = elem;
            }
            this.setSize(data.length);
        }
    }


    public int getSize() { return this.size; }
    @Override public List getHead() { return this.head; }
    @Override public List getTail() { return this.tail; }

    public void setSize(final int size) { this.size = size; }

    @Override
    public void setHead(List elem) {
        super.setHead(elem);
        this.head = elem;
    }

    @Override
    public void setTail(List elem) {
        super.setTail(elem);
        this.tail = elem;
    }
}
