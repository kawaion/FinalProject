package Sorters;

import collection.CustomList;

import java.util.Comparator;

public interface Sorter<E> {
    CustomList<E> sort(CustomList<E> list, Comparator<E> comparator);
}
