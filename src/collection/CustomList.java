package org.top.collection;

import java.util.Arrays;
import java.util.Comparator;

import org.top.strategy.Sorter;

public class CustomList<E> {
    private final int DEFAULT_CAPACITY = 8;
    private Object[] objects;
    private int size;

    public CustomList() {
        this.init();
    }

    private void init() {
        this.objects = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index > this.size + 1)
            throw new IndexOutOfBoundsException(String.format("Индекс: %d, размер коллекции: %d, последний индекс: %d",
                    index, this.size(), this.size));
    }

    private void checkIndexAdd(int index) {
        if (index < 0 || index > this.size + 1)
            throw new IndexOutOfBoundsException(String.format("Индекс: %d, размер коллекции: %d, последний индекс: %d",
                    index, this.size(), this.size));
    }

    public int size() {
        return this.size + 1;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private void resize(int newsize) {
        int newShift = 0;
        int currentSize = DEFAULT_CAPACITY;
        while (newsize >= currentSize) {
            newShift++;
            currentSize = DEFAULT_CAPACITY << newShift;
        }
        if (currentSize != objects.length)
            objects = Arrays.copyOf(objects, newSize);
    }

    public boolean add(E e) {
        this.resize(++size);
        objects[size] = e;
        return true;
    }

    public void clear() {
        init();
    }

    public E get(int index) {
        this.checkIndex(index);
        return (E) objects[index];
    }

    public E set(int index, E element) {
        this.checkIndex(index);
        E result = (E) objects[index];
        objects[index] = element;
        return result;
    }

    public void add(int index, E element) {
        this.checkIndexAdd(index);
        resize(++size);
        System.arraycopy(objects, index, objects, index + 1, size - index);
        objects[index] = element;
    }

    public E remove(int index) {
        this.checkIndex(index);
        E result = (E) objects[index];
        for (int i = index; i < size; i++)
            objects[i] = objects[i + 1];
        objects[size] = null;
        resize(--size);
        return result;
    }

    public void sort(Comparator<? super E> c) {
        Arrays.sort((E[]) objects, 0, this.size(), c);
    }

    public void customSort(Sorter<E> sorter, Comparator<E> comparator) {
        sorter.sort(this, comparator);
    }
}