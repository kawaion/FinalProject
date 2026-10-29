package Sorting;

import java.util.Comparator;

public class QuickSort<T> implements SortStrategy<T> {
    private Comparator<T> cmp;
    private SortableList<T> worklist;

    @Override
    public SortableList<T> sort(SortableList<T> list, Comparator<T> comparator) {
        this.cmp = comparator;
        this.worklist = list;
        sortRange(0, list.size());
        return list;
    }

    private void sortRange(int lo, int hi) {
        if (hi - lo <= 1) return;
        int p = partition(lo, hi);
        sortRange(lo, p);
        sortRange(p + 1, hi);
    }

    private int partition(int lo, int hi) {
        var pivot = worklist.get(hi - 1);
        int i = lo;
        for (int j = lo; j < hi - 1; j++) {
            if (cmp.compare(worklist.get(j), pivot) <= 0) {
                swap(i, j);
                i++;
            }
        }
        swap(i, hi - 1);
        return i;
    }

    private void swap(int i, int j) {
        var el = worklist.get(i);
        worklist.set(i, worklist.get(j));
        worklist.set(j, el);
    }
}
