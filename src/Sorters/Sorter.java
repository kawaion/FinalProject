package Sorters;

import java.util.Comparator;

public interface Sorter<E> {
    CustomList<E> sort(CustomList<E> list, Comparator<E> comparator);
}
