package org.top.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.top.strategy.Sorter;

public class CustomList<E> implements List<E> {
    private final int START_SIZE = 8;
    private Object[] objects;
    private int lastIndex;

    public CustomList() {
        this.init();
    }

    private void init() {
        this.objects = new Object[START_SIZE];
        this.lastIndex = -1;
    }

    private void checkIndex(int index) {
        if (index < 0 || index > this.lastIndex)
            throw new IndexOutOfBoundsException(String.format("Индекс: %d, размер коллекции: %d, последний индекс: %d",
                    index, this.size(), this.lastIndex));
    }

    private void checkIndexAdd(int index) {
        if (index < 0 || index > this.lastIndex + 1)
            throw new IndexOutOfBoundsException(String.format("Индекс: %d, размер коллекции: %d, последний индекс: %d",
                    index, this.size(), this.lastIndex));
    }

    @Override
    public int size() {
        return this.lastIndex + 1;
    }

    @Override
    public boolean isEmpty() {
        return this.lastIndex == -1;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(objects, this.lastIndex + 1);
    }

    @Override
    public boolean contains(Object o) {
        if (this.indexOf(o) >= 0)
            return true;
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<E> {
        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor <= lastIndex;
        }

        @Override
        public E next() {
            if (!this.hasNext())
                throw new NoSuchElementException("Элементы в списке закончились");
            return (E) objects[cursor++];
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size())
            return (T[]) Arrays.copyOf(objects, size(), a.getClass());
        System.arraycopy(objects, 0, a, 0, this.size());
        if (this.lastIndex > -1 && a.length > this.lastIndex)
            a[this.lastIndex + 1] = null;
        return a;
    }

    private void resize(int newLastIndex) {
        int newShift = 0;
        int newSize = START_SIZE;
        while (newLastIndex >= newSize) {
            newShift++;
            newSize = START_SIZE << newShift;
        }
        if (newSize != objects.length)
            objects = Arrays.copyOf(objects, newSize);
    }

    @Override
    public boolean add(E e) {
        this.resize(++lastIndex);
        objects[lastIndex] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = this.indexOf(o);
        if (index >= 0)
            this.remove(index);
        return index >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!this.contains(e))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty())
            return false;
        for (E e : c)
            this.add(e);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.checkIndexAdd(index);
        if (c.isEmpty())
            return false;
        int numMoved = lastIndex + 1 - index;
        lastIndex += c.size();
        resize(lastIndex);
        if (numMoved > 0)
            System.arraycopy(objects, index, objects, index + c.size(), numMoved);

        for (E e : c) {
            objects[index] = e;
            index++;
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty())
            return false;
        boolean modified = false;
        for (int i = lastIndex; i >= 0; i--)
            if (c.contains(objects[i])) {
                this.remove(i);
                modified = true;
            }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i = lastIndex; i >= 0; i--)
            if (!c.contains(objects[i])) {
                this.remove(objects[i]);
                modified = true;
            }
        return modified;
    }

    @Override
    public void clear() {
        init();
    }

    @Override
    public E get(int index) {
        this.checkIndex(index);
        return (E) objects[index];
    }

    @Override
    public E set(int index, E element) {
        this.checkIndex(index);
        E result = (E) objects[index];
        objects[index] = element;
        return result;
    }

    @Override
    public void add(int index, E element) {
        this.checkIndexAdd(index);
        resize(++lastIndex);
        System.arraycopy(objects, index, objects, index + 1, lastIndex - index);
        objects[index] = element;
    }

    @Override
    public E remove(int index) {
        this.checkIndex(index);
        E result = (E) objects[index];
        for (int i = index; i < lastIndex; i++)
            objects[i] = objects[i + 1];
        objects[lastIndex] = null;
        resize(--lastIndex);
        return result;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i <= lastIndex; i++)
            if (Objects.equals(objects[i], o))
                return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = lastIndex; i >= 0; i--)
            if (Objects.equals(objects[i], o))
                return i;
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new CustomListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new CustomListIterator(index);
    }

    private class CustomListIterator implements ListIterator<E> {
        private int cursor;
        private int lastCursor = -1;

        public CustomListIterator(int index) {
            checkIndexAdd(index);
            this.cursor = index;
        }

        @Override
        public boolean hasNext() {
            return this.cursor <= lastIndex;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastCursor = cursor;
            return (E) objects[cursor++];
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            lastCursor = --cursor;
            return (E) objects[cursor];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (lastCursor < 0)
                throw new IllegalStateException();
            CustomList.this.remove(lastCursor);
            cursor = lastCursor;
            lastCursor = -1;
        }

        @Override
        public void set(E e) {
            if (lastCursor < 0)
                throw new IllegalStateException();
            objects[lastCursor] = e;
        }

        @Override
        public void add(E e) {
            CustomList.this.add(cursor, e);
            cursor++;
            lastCursor = -1;
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        this.checkIndex(fromIndex);
        this.checkIndexAdd(toIndex);

        CustomList<E> sub = new CustomList<>();
        for (int i = fromIndex; i < toIndex; i++)
            sub.add((E) objects[i]);
        return sub;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        Arrays.sort((E[]) objects, 0, this.size(), c);
    }

    public void customSort(Sorter<E> sorter, Comparator<E> comparator) {
        sorter.sort(this, comparator);
    }
}