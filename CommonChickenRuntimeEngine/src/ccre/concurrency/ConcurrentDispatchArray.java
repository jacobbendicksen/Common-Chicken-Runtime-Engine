/*
 * Copyright 2013 Colby Skeggs
 * 
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 * 
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package ccre.concurrency;

import ccre.util.CArrayUtils;
import ccre.util.CCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A concurrent collection that allows concurrent iteration and removal without
 * concurrency errors. The values returned by an iterator are the values that
 * were in the iterator when the iterator was started.
 *
 * This is implemented by copying the entire array when a modification operation
 * is completed.
 *
 * @author skeggsc
 * @param <E> The type of the collection's elements
 */
public class ConcurrentDispatchArray<E> implements CCollection<E> {

    /**
     * The array that contains the current data. Do not modify this field
     * directly - use compareAndSetArray.
     *
     * @see #compareAndSetArray(java.lang.Object[], java.lang.Object[])
     */
    protected volatile Object[] data = new Object[0];

    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Object[] dat = data;
            int i = 0;

            public boolean hasNext() {
                return i < dat.length;
            }

            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E e = (E) dat[i++];
                return e;
            }

            public void remove() {
                Object tgt = dat[i - 1];
                Object[] old, dout;
                do {
                    old = data;
                    dout = new Object[old.length - 1];
                    int j;
                    for (j = 0; j < old.length; j++) {
                        Object cur = old[j];
                        if (cur == tgt) {
                            break;
                        }
                        dout[j] = cur;
                    }
                    if (j == old.length) {
                        // already removed. do nothing!
                        return;
                    }
                    for (j++; j < old.length; j++) {
                        dout[j - 1] = old[j];
                    }
                } while (!compareAndSetArray(old, dout));
            }
        };
    }

    /**
     * If the current array is the expected array, set the current array to the
     * updated array.
     *
     * As long as all modifications occur through this method, there will be
     * race conditions or deadlocks.
     *
     * @param expect the array that is expected to be the current value.
     * @param update the array that should replace the current array.
     * @return if the replacement completed successfully.
     */
    protected synchronized boolean compareAndSetArray(Object[] expect, Object[] update) {
        if (expect == data) {
            data = update;
            return true;
        } else {
            return false;
        }
    }

    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        while (true) {
            Object[] old = data;
            Object[] dout = CArrayUtils.copyOf(old, old.length + 1);
            dout[dout.length - 1] = e;
            if (compareAndSetArray(old, dout)) {
                return true;
            }
        }
    }

    public int size() {
        return data.length;
    }

    public boolean remove(Object o) {
        for (Iterator<E> it = this.iterator(); it.hasNext();) {
            E e = it.next();
            if (e.equals(o)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void clear() {
        data = new Object[data.length];
    }

    public boolean addAll(CCollection<? extends E> c) {
        boolean out = false;
        for (E e : c) {
            out |= add(e);
        }
        return out;
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public boolean contains(Object o) {
        for (E cur : this) {
            if (cur.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(CCollection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean removeAll(CCollection<?> c) {
        boolean mod = false;
        for (Iterator<E> it = this.iterator(); it.hasNext();) {
            E e = it.next();
            if (c.contains(e)) {
                it.remove();
                mod = true;
            }
        }
        return mod;
    }

    public boolean retainAll(CCollection<?> c) {
        boolean mod = false;
        for (Iterator<E> it = this.iterator(); it.hasNext();) {
            E e = it.next();
            if (!c.contains(e)) {
                it.remove();
                mod = true;
            }
        }
        return mod;
    }
}