package test.sorter;

import org.junit.jupiter.api.Test;
import sorter.QuickSort;
import sorter.SortStrategy;

class QuickSortTest extends AbstractSortStrategyTest {

    @Override
    protected SortStrategy<Integer> createSorter() {
        return new QuickSort<>();
    }

    @Override
    protected SortStrategy<String> createStringSorter() {return new QuickSort<>(); }

    @Test
    void threeElementsPermutationKept() {
        var list = listOf(2, 3, 1);
        new QuickSort<Integer>().sort(list, NATURAL);
        assertElements(list, 1, 2, 3);
    }
}
