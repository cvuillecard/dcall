package com.dcall.core.configuration.utils.tree;

public interface BTree<T> {
    // getter
    T getData();
    BTree<T> getLeft();
    BTree<T> getRight();
    BTree<T> getParent();

    // setter
    BTree<T> setData(final T data);
    BTree<T> setLeft(final BTree<T> left);
    BTree<T> setRight(final BTree<T> right);
    BTree<T> setParent(final BTree<T> parent);
}
