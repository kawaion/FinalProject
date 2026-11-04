package collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import sorters.SortStrategy;

public class SortableList<E> {
    private static final int DEFAULT_CAPACITY = 8;
    private Object[] objects;
    private int size;

    public SortableList() {
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

    public SortableList<E> customSort(SortStrategy<E> sortStrategy, Comparator<E> comparator) {
        return sortStrategy.sort(this, comparator);
    }

    private Spliterator<E> spliterator() {
        return Spliterators.spliterator((E[]) objects, 0, size,
                Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED);
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public static <T> Collector<T, ?, SortableList<T>> toSortableList() {
        return Collector.of(
                SortableList::new,
                SortableList::add,
                (left, right) -> {
                    for (int i = 0; i < right.size(); i++) {
                        left.add(right.get(i));
                    }
                    return left;
                },
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(objects[i]).append('\n');
        }
        return sb.toString();
    }
}