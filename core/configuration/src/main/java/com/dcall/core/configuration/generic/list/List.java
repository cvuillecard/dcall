package com.dcall.core.configuration.generic.list;

import java.io.Serializable;

public interface List extends Serializable {

    // code
    void insertAfter(final List elem);
    void insertBefore(final List elem);
    void push(final List elem);
    void pushFront(final List elem);
    void pushBack(final List elem);
    void pushAll(final List... list);
    List getAt(final int pad);
    List getAtBefore(final int pad);
    List getAtAfter(final int pad);
    void insertBeforeElem(final List mntPtr, final List toAdd);
    void insertBeforeAt(final int idx, final List elem);
    void insertAfterElem(final List mntPtr, final List toAdd);
    void insertAfterAt(final int idx, final List elem);
    void insertAllAfterElem(final List mntPtr, final List... list);
    void insertAllAfterAt(final int idx, final List... list);
    void insertAllBeforeElem(final List mntPtr, final List... list);
    void insertAllBeforeAt(final int idx, final List... list);
    void pop();
    void popFront();
    void popBack();
    List find(final List currPtr, final List elem); // FindAt / FindAllAt / FindInRange
    List findAt(final int idx, final List elem);
    List findAfter(final List currPtr, final List elem);
    List findAfterAt(final int idx, final List elem);
    List findBefore(final List currPtr, final List elem);
    List findBeforeAt(final int idx, final List elem);
    void delete(final List elem);
    void deleteAt(final int idx, final List elem);
    void deleteAfter(final List elem);
    void deleteAfterAt(final int idx, final List elem);
    void deleteBefore(final List elem);
    void deleteBeforeAt(final int idx, final List elem);
    void deleteAll(final List... list);
    void deleteAllAt(final int idx, final List... list);
    void clear();
    void clearBefore();
    void clearAfter();
    int count();
    void reverse();

    // getters
    List getHead();
    List getTail();
    List getNext();
    List getPrev();
    <T> T getData();

    // setters
    void setHead(final List elem);
    void setTail(final List elem);
    void setNext(final List next);
    void setPrev(final List prev);
    <T> void setData(final T data);
}
