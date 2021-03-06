/*
 * Copyright 2013-2014 Colby Skeggs
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
package ccre.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An abstract list. This is the superclass of the list implementations in this
 * package.
 *
 * @author skeggsc
 * @param <T> the element type.
 */
public abstract class CAbstractList<T> implements CList<T> {

    /**
     * The number of times that the list has been modified. This is used to
     * ensure that changes are not made during iteration.
     */
    private int modCount = 0;
    private final Object modCountLock = new Object();

    /**
     * Increment the modification count.
     *
     * @see #modCount
     */
    protected void notifyModified() {
        synchronized (modCountLock) {
            modCount++;
        }
    }

    /**
     * Get the current modification count - the number of times that the list
     * has been modified.
     *
     * @return the modcount.
     */
    public int getModCount() {
        return modCount;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;
            private int locmod = getModCount();

            public boolean hasNext() {
                if (locmod != getModCount()) {
                    throw new ConcurrentModificationException("Modcount is " + getModCount() + " instead of " + locmod);
                }
                return i < size();
            }

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(i++);
            }

            public void remove() {
                CAbstractList.this.remove(--i);
                locmod++;
            }
        };
    }

    public boolean add(T e) {
        add(size(), e);
        return true;
    }

    public boolean remove(Object o) {
        int idx = indexOf(o);
        if (idx == -1) {
            return false;
        }
        remove(idx);
        return true;
    }

    public boolean containsAll(CCollection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(CCollection<? extends T> c) {
        boolean mod = false;
        for (T o : c) {
            mod |= add(o);
        }
        return mod;
    }

    public boolean addAll(int startAt, CCollection<? extends T> c) {
        int index = startAt;
        for (T o : c) {
            add(index++, o);
        }
        return !c.isEmpty();
    }

    public boolean removeAll(CCollection<?> c) {
        boolean mod = false;
        for (Object o : c) {
            mod |= remove(o);
        }
        return mod;
    }

    public boolean retainAll(CCollection<?> c) {
        Iterator<T> itr = this.iterator();
        boolean mod = false;
        while (itr.hasNext()) {
            T o = itr.next();
            if (!c.contains(o)) {
                itr.remove();
                mod = true;
            }
        }
        return mod;
    }

    public void clear() {
        while (!isEmpty()) {
            remove(0);
        }
    }

    public T set(int index, T element) {
        throw new UnsupportedOperationException("Element modification not supported.");
    }

    public void add(int index, T element) {
        throw new UnsupportedOperationException("Element addition not supported.");
    }

    public T remove(int index) {
        throw new UnsupportedOperationException("Element removal not supported.");
    }

    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size(); i++) {
                if (get(i) == null) {
                    return i;
                }
            }
            return -1;
        } else {
            for (int i = 0; i < size(); i++) {
                if (o.equals(get(i))) {
                    return i;
                }
            }
            return -1;
        }
    }

    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size() - 1; i >= 0; i--) {
                if (get(i) == null) {
                    return i;
                }
            }
            return -1;
        } else {
            for (int i = size() - 1; i >= 0; i--) {
                if (o.equals(get(i))) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object o : this) {
            if (o == null) {
                sb.append("null, ");
            } else {
                sb.append(o.toString()).append(", ");
            }
        }
        if (sb.length() <= 1) {
            return "[]";
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }

    public Object[] toArray() {
        Object[] out = new Object[size()];
        if (fillArray(out) != 0) {
            throw new ConcurrentModificationException("Modification during toArray!");
        }
        return out;
    }

    public int fillArray(Object[] target) {
        Iterator<T> it = this.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i < target.length) {
                target[i] = it.next();
            } else {
                it.next();
            }
            i++;
        }
        return i - target.length;
    }
}
