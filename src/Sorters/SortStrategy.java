package Sorters;

import collection.SortableList;

import java.util.Comparator;

public interface SortStrategy<E> {
    SortableList<E> sort(SortableList<E> list, Comparator<E> comparator);
}
