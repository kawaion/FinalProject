package test.sorters;

import org.junit.jupiter.api.Test;
import sorters.QuickSort;
import sorters.SortStrategy;

class QuickSortTest extends AbstractSortStrategyTest {

    @Override
    protected SortStrategy<Integer> createSorter() {
        return new QuickSort<>();
    }

    @Test
    void threeElementsPermutationKept() {
        var list = listOf(2, 3, 1);
        new QuickSort<Integer>().sort(list, NATURAL);
        assertElements(list, 1, 2, 3);
    }
}
