package com.dcall.core.configuration.utils.list;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractList implements com.dcall.core.configuration.utils.list.List {
    private List next = null;
    private List prev = null;
    private Object data = null;

    public AbstractList() {}

    public <T> AbstractList(T data) {
        this.setData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractList)) return false;
        AbstractList that = (AbstractList) o;
        return Objects.equals(getNext(), that.getNext()) &&
                Objects.equals(getPrev(), that.getPrev()) &&
                Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNext(), this.getPrev(), this.getData());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        List ptr = this.getHead();

        while (ptr.getNext() != null) {
            sb.append(ptr.toString());
            ptr =  ptr.getNext();
        }

        return sb.toString();
    }

    @Override
    public void insertAfter(final List elem) {
        if (elem != null) {
            this.setNext(elem);
            elem.setPrev(this);
        }
    }

    @Override
    public void insertBefore(final List elem) {
        if (elem != null) {
            this.setPrev(elem);
            elem.setNext(this);
        }
    }

    // FUNCTIONS
    @Override
    public void push(final List elem) {
        if (elem == null) return;
        this.getTail().insertAfter(elem);
    }

    @Override
    public void pushFront(final List elem) {
        if (elem == null) return;
        this.getHead().insertBefore(elem);
    }

    @Override
    public void pushBack(final List elem) {
        this.push(elem);
    }

    @Override
    public void pushAll(final List... list) {
        if (list != null && list.length > 0) {
            List tail;

            for (int i = 0; i < list.length - 1; i++) {
                tail = list[i].getTail();
                tail.push(list[i + 1]);
            }

            this.getTail().push(list[0]);
        }
    }

    @Override
    public List getAt(final int pad) {
        if (pad < 0) return null;
        int i = 0;
        List ptr = this.getHead();

        while (ptr.getNext() != null && i < pad) {
            ptr = ptr.getNext();
            i += 1;
        }

        if (i != pad)
            throw new IndexOutOfBoundsException(" list size : " + i + " / idx : " + pad);

        return ptr;
    }

    @Override
    public List getAtBefore(final int pad) {
        if (pad < 0) return null;
        int i = 0;
        List ptr = this;

        while (ptr.getPrev() != null && i < pad) {
            ptr = ptr.getPrev();
            i++;
        }

        if (i != pad)
            throw new IndexOutOfBoundsException(" No more elements to iterate - nb elements iterated : " + i + " / padding : " + pad);

        return ptr;
    }

    @Override
    public List getAtAfter(final int pad) {
        if (pad < 0) return null;
        int i = 0;
        List ptr = this;

        while (ptr.getNext() != null && i < pad) {
            ptr = ptr.getNext();
            i++;
        }

        if (i != pad)
            throw new IndexOutOfBoundsException(" No more elements to iterate - nb elements iterated : " + i + " / padding : " + pad);

        return ptr;
    }


    @Override
    public void insertBeforeElem(final List mntPtr, final List toAdd) {
        if (toAdd == null) return;
        final List ptr = this.find(this, mntPtr);

        if (ptr != null) {
            if (ptr.getPrev() != null)
                ptr.getPrev().insertAfter(toAdd);
            ptr.insertBefore(toAdd);
        }
    }

    @Override
    public void insertBeforeAt(final int idx, final List elem) {
        if (elem == null) return;
        final List ptr = this.getAt(idx);

        if (ptr != null) {
            if (ptr.getPrev() != null)
                ptr.getPrev().insertAfter(elem);
            ptr.insertBefore(elem);
        }
    }

    @Override
    public void insertAfterElem(final List mntPtr, final List toAdd) {
        if (toAdd == null) return;
        final List ptr = this.find(this, mntPtr);

        if (ptr != null) {
            if (ptr.getNext() != null)
                ptr.getNext().insertBefore(toAdd);
            ptr.insertAfter(toAdd);
        }
    }

    @Override
    public void insertAfterAt(final int idx, final List elem) {
        if (elem == null) return;
        final List ptr = this.getAt(idx);

        if (ptr != null) {
            if (ptr.getNext() != null)
                ptr.getNext().insertBefore(elem);
            ptr.insertAfter(elem);
        }
    }

    @Override
    public void insertAllAfterElem(final List mntPtr, final List... list) {
        if (mntPtr != null && list != null && list.length > 0) {
            final List ptr = this.find(this, mntPtr);
            final List headList = list[0];
            List tailList;

            if (ptr != null) {
                if (list.length > 1)
                    headList.pushAll(Arrays.copyOfRange(list, 1, list.length));
                tailList = list[list.length - 1].getTail();
                if (ptr.getNext() != null)
                    ptr.getNext().insertBefore(tailList);
                ptr.insertAfter(headList);
            }
        }
    }

    @Override
    public void insertAllAfterAt(final int idx, final List... list) {
        this.insertAllAfterElem(this.getAt(idx), list);
    }

    @Override
    public void insertAllBeforeElem(final List mntPtr, final List... list) {
        if (mntPtr != null && list != null && list.length > 0) {
            final List ptr = this.find(this, mntPtr);
            final List headList = list[0];
            List tailList;

            if (ptr != null) {
                if (list.length > 1)
                    headList.pushAll(Arrays.copyOfRange(list, 1, list.length));
                tailList = list[list.length - 1].getTail();
                if (ptr.getPrev() != null)
                    ptr.getPrev().insertAfter(headList);
                ptr.insertBefore(tailList);
            }
        }
    }

    @Override
    public void insertAllBeforeAt(final int idx, final List... list) {
        this.insertAllBeforeElem(this.getAt(idx), list);
    }

    @Override
    public void pop() {
        final List tail = this.getTail();

        if (tail.getPrev() != null) {
            tail.getPrev().delete(tail);
        }
    }

    @Override
    public void popFront() {
        final List head = this.getHead();

        if (head.getNext() != null)
            head.getNext().deleteBefore(head);
    }

    @Override
    public void popBack() {
        this.pop();
    }

    @Override
    public List find(final List currPtr, final List elem) {
        if (currPtr == null || elem == null)
            return null;

        if (currPtr.equals(elem))
            return elem;

        List ptr = currPtr;

        while (!ptr.equals(elem) && ptr.getNext() != null)
            ptr = ptr.getNext();

        if (!ptr.equals(elem)) {
            ptr = currPtr;
            while (!ptr.equals(elem) && ptr.getPrev() != null)
                ptr = ptr.getPrev();
        }

        return ptr.equals(elem) ? ptr : null;
    }

    @Override
    public List findAt(final int idx, final List elem) {
        return this.find(this.getAt(idx), elem);
    }

    @Override
    public List findAfter(final List currPtr, final List elem) {
        if (currPtr == null || elem == null)
            return null;

        if (currPtr.equals(elem))
            return elem;

        List ptr = currPtr;

        while (!ptr.equals(elem) && ptr.getNext() != null)
            ptr = ptr.getNext();

        return ptr.equals(elem) ? ptr : null;
    }

    @Override
    public List findAfterAt(final int idx, final List elem) {
        return this.findAfter(this.getAt(idx), elem);
    }

    @Override
    public List findBefore(final List currPtr, final List elem) {
        if (currPtr == null || elem == null)
            return null;

        if (currPtr.equals(elem))
            return elem;

        List ptr = currPtr;

        while (!ptr.equals(elem) && ptr.getPrev() != null)
            ptr = ptr.getPrev();

        return ptr.equals(elem) ? ptr : null;
    }

    @Override
    public List findBeforeAt(final int idx, final List elem) {
        return this.findBefore(this.getAt(idx), elem);
    }

    @Override
    public void delete(final List elem) {
        this.remove(this.find(this, elem));
    }

    @Override
    public void deleteAt(final int idx, final List elem) {
        if (idx >= 0) {
            final List ptr = this.getAt(idx);
            if (ptr != null)
                this.remove(ptr.find(ptr, elem));
        }
    }

    @Override
    public void deleteAfter(final List elem) {
        this.remove(this.findAfter(this, elem));
    }

    @Override
    public void deleteAfterAt(final int idx, final List elem) {
        if (idx >= 0) {
            final List ptr = this.getAt(idx);
            if (ptr != null)
                ptr.deleteAfter(elem);
        }
    }

    @Override
    public void deleteBefore(final List elem) {
        this.remove(this.findBefore(this, elem));
    }

    @Override
    public void deleteBeforeAt(final int idx, final List elem) {
        if (idx >= 0) {
            final List ptr = this.getAt(idx);
            if (ptr != null)
                ptr.deleteBefore(elem);
        }
    }

    private void remove(List elem) {
        if (elem != null) {
            if (elem.getPrev() != null)
                elem.getPrev().setNext(elem.getNext());

            if (elem.getNext() != null)
                elem.getNext().setPrev(elem.getPrev());

            elem.setNext(null);
            elem.setPrev(null);
            elem.setData(null);

            elem = (elem != this) ? null : this;
        }
    }

    @Override
    public void deleteAll(final List... list) {
        if (list != null && list.length > 0) {
            for (int i = 0; i < list.length; i++) {
                List head = (list[i] != null) ? list[i].getHead() : null;
                List ptr = head;
                while (ptr != null && ptr.getNext() != null) {
                    final List next = ptr.getNext();
                    next.deleteBefore(ptr);
                    ptr = next;
                }
                this.delete(ptr);
            }
        }
    }

    @Override
    public void deleteAllAt(final int idx, final List... list) {
        if (idx >= 0 && list != null && list.length > 0) {
            final List ptr = this.getAt(idx);
            if (ptr != null)
                ptr.deleteAll(list);
        }
    }

    @Override
    public void clear() {
        this.clearBefore();
        this.clearAfter();
        this.setData(null);
    }

    public void clearBefore() {
        List ptr = this;

        while (ptr != null && ptr.getPrev() != null) {
            final List prev = ptr.getPrev().getPrev();
            ptr.deleteBefore(ptr.getPrev());
            ptr = prev;
        }

        this.prev = null;
    }

    public void clearAfter() {
        List ptr = this;

        while (ptr != null && ptr.getNext() != null) {
            final List next = ptr.getNext().getNext();
            ptr.deleteAfter(ptr.getNext());
            ptr = next;
        }

        this.next = null;
    }

    @Override
    public int count() {
        int i = 1;
        List ptr = this.getHead();

        if (ptr == null) return i;

        while (ptr.getNext() != null) {
            ptr = ptr.getNext();
            i += 1;
        }

        return i;
    }

    @Override
    public void reverse() {
        final List head = this.getTail();
        List tail = head;
        List prev = tail.getPrev();
        List nextPrev;

        while (prev != null) {
            nextPrev = prev.getPrev();
            prev.setNext(null);
            tail.pushBack(prev);
            tail = prev;
            prev = nextPrev;
        }
        head.setPrev(null);
    }

    // GETTERS
    @Override public List getHead() {
        List ptr = this;

        while (ptr.getPrev() != null)
            ptr = ptr.getPrev();

        return ptr;
    }

    @Override public List getTail() {
        List ptr = this;

        while (ptr.getNext() != null)
            ptr = ptr.getNext();

        return ptr;
    }

    @Override public List getNext() { return this.next; }
    @Override public List getPrev() { return this.prev; }
    @Override public <T> T getData() { return (T) this.data; }

    // SETTERS
    @Override public void setHead(final List elem) {
        if (elem != null)
            this.getHead().insertBefore(elem);
    }

    @Override public void setTail(final List elem) {
        if (elem != null)
            this.getTail().insertAfter(elem);
    }

    @Override public void setNext(final List next) { this.next = next; }
    @Override public void setPrev(final List prev) { this.prev = prev; }
    @Override public <T> void setData(final T data) { this.data = data; }
}

