package org.top.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.top.strategy.Sorter;

public class CustomList<E> {
    private static final int DEFAULT_CAPACITY = 8;
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
        if (index < 0 || index >= this.size)
            throw new IndexOutOfBoundsException(String.format(
                "Индекс: %d, размер коллекции: %d, допустимый диапазон: 0..%d",
                index, this.size, this.size - 1));
    }

    private void checkIndexAdd(int index) {
        if (index < 0 || index > this.size)
            throw new IndexOutOfBoundsException(String.format(
                "Индекс вставки: %d, размер коллекции: %d, допустимый диапазон: 0..%d",
                index, this.size, this.size));
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private boolean ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > objects.length) {
            grow(requiredCapacity);
            return true;
        }
        return false;
    }

    private void grow(int minCapacity) {
        int newCapacity = Math.max(DEFAULT_CAPACITY, objects.length);
        while (newCapacity < minCapacity)
            newCapacity <<= 1;
        if (newCapacity != objects.length)
            objects = Arrays.copyOf(objects, newCapacity);
    }

    public boolean add(E e) {
        this.ensureCapacity(size + 1);
        size++;
        int lastIndex = size - 1;
        objects[lastIndex] = e;
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
        ensureCapacity(size + 1);
        System.arraycopy(objects, index, objects, index + 1, size - index);        
        size++;
        objects[index] = element;
    }

    public E remove(int index) {
        this.checkIndex(index);
        E result = (E) objects[index];
        for (int i = index; i < size - 1; i++)
            objects[i] = objects[i + 1];
        int lastIndex = size - 1;
        objects[lastIndex] = null;
        size--;
        return result;
    }

    public void sort(Comparator<? super E> c) {
        Arrays.sort((E[]) objects, 0, this.size, c);
    }

    public void customSort(Sorter<E> sorter, Comparator<E> comparator) {
        sorter.sort(this, comparator);
    }

    // ==================== Stream API ====================

    private Spliterator<E> spliterator() {
        return Spliterators.spliterator((E[]) objects, 0, size,
                Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED);
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public static <T> Collector<T, ?, CustomList<T>> toCustomList() {
        return Collector.of(
                CustomList::new,     // supplier — создаёт пустой список
                CustomList::add,     // accumulator — добавляет элементы по одному
                (left, right) -> {   // combiner — сливает части при параллельной сборке
                    for (int i = 0; i < right.size(); i++) {
                        left.add(right.get(i));
                    }
                    return left;
                },
                Collector.Characteristics.IDENTITY_FINISH  // контейнер уже CustomList — финишер не нужен
        );
    }
}