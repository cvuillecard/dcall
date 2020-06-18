package com.dcall.core.configuration.generic.parser;

import com.dcall.core.configuration.utils.tree.AbstractBTree;

public class Node<T> extends AbstractBTree<T> {
    Node() { super(); }
    Node(final T data) { super(data); }
    Node(T data, AbstractBTree left, AbstractBTree right) { super(data, left, right); }
    Node(T data, AbstractBTree left, AbstractBTree right, AbstractBTree parent) { super(data, left, right, parent); }
}
