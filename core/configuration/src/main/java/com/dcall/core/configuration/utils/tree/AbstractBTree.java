package com.dcall.core.configuration.utils.tree;

public abstract class AbstractBTree<T> implements BTree<T> {
    private T data;
    private BTree<T> left;
    private BTree<T> right;
    private BTree<T> parent;

    public AbstractBTree() {}
    public AbstractBTree(final T data)
    { this.data = data; }
    public AbstractBTree(final T data, final BTree left, final BTree right)
    { this.data = data; this.left = left; this.right = right; }
    public AbstractBTree(final T data, BTree parent, final BTree left, final BTree right)
    { this.data = data; this.parent = parent; this.left = left; this.right = right; }

    // getter
    @Override public T getData() { return  data; }
    @Override public BTree<T> getLeft() { return left; }
    @Override public BTree<T> getRight() { return right; }
    @Override public BTree<T> getParent() { return parent; }

    // setter
    @Override public BTree<T> setData(final T data) { this.data = data; return this; }
    @Override public BTree<T> setLeft(final BTree<T> left) { this.left = left; left.setParent(this); return this; }
    @Override public BTree<T> setRight(final BTree<T> right) { this.right = right; right.setParent(this); return this; }
    @Override public BTree<T> setParent(final BTree<T> parent) { this.parent= parent; return this; }
}
