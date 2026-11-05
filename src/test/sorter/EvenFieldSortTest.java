package test.sorter;

import collection.SortableList;
import org.junit.jupiter.api.Test;
import sorter.EvenFieldSort;
import sorter.MergeSort;
import sorter.QuickSort;
import sorter.SortStrategy;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class EvenFieldSortTest {

    static final Comparator<Integer> NATURAL = Comparator.naturalOrder();
    static final Comparator<Integer> REVERSE = Comparator.reverseOrder();

    static SortableList<Integer> listOf(Integer... values) {
        SortableList<Integer> list = new SortableList<>();
        for (Integer v : values) list.add(v);
        return list;
    }

    static void assertElements(SortableList<Integer> list, Integer... expected) {
        assertEquals(expected.length, list.size(), "размеры не совпадают");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], list.get(i), "расхождение в индексе " + i);
        }
    }

    private EvenFieldSort<Integer> sorterBySelf() {
        return new EvenFieldSort<>(new MergeSort<>(), x -> x);
    }

    @Test
    void emptyList() {
        SortableList<Integer> list = listOf();
        assertSame(list, sorterBySelf().sort(list, NATURAL));
        assertElements(list);
    }

    @Test
    void singleOddElement() {
        assertElements(sorterBySelf().sort(listOf(3), NATURAL), 3);
    }

    @Test
    void singleEvenElement() {
        assertElements(sorterBySelf().sort(listOf(4), NATURAL), 4);
    }

    @Test
    void allOddUnchangedAndSameInstance() {
        SortableList<Integer> list = listOf(5, 3, 1, 7);
        assertSame(list, sorterBySelf().sort(list, NATURAL));
        assertElements(list, 5, 3, 1, 7);
    }

    @Test
    void allEvenWholeListSorted() {
        assertElements(sorterBySelf().sort(listOf(8, 4, 6, 2), NATURAL), 2, 4, 6, 8);
    }

    @Test
    void twoElementsOddEvenUnchanged() {
        assertElements(sorterBySelf().sort(listOf(1, 2), NATURAL), 1, 2);
    }

    @Test
    void twoElementsEvenOddUnchanged() {
        assertElements(sorterBySelf().sort(listOf(2, 1), NATURAL), 2, 1);
    }

    @Test
    void onlyEvenPositionsChanged() {
        assertElements(sorterBySelf().sort(listOf(4, 5, 2, 7, 8, 1, 6), NATURAL),
                2, 5, 4, 7, 6, 1, 8);
    }

    @Test
    void alreadySortedEvensUnchanged() {
        assertElements(sorterBySelf().sort(listOf(2, 1, 4, 3, 6, 5), NATURAL),
                2, 1, 4, 3, 6, 5);
    }

    @Test
    void reverseComparatorOnlyAffectsEvens() {
        assertElements(sorterBySelf().sort(listOf(4, 5, 2, 7, 8, 1, 6), REVERSE),
                8, 5, 6, 7, 4, 1, 2);
    }

    @Test
    void mixedDuplicates() {
        assertElements(sorterBySelf().sort(listOf(2, 4, 1, 2, 3, 4), NATURAL),
                2, 2, 1, 4, 3, 4);
    }

    @Test
    void returnsSameInstance() {
        SortableList<Integer> list = listOf(4, 3, 2, 1);
        assertSame(list, sorterBySelf().sort(list, NATURAL));
    }

    @Test
    void delegateIsActuallyUsed() {
        SortStrategy<Integer> reversingStub = (list, comparator) -> {
            for (int i = 0, j = list.size() - 1; i < j; i++, j--) {
                Integer tmp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, tmp);
            }
            return list;
        };
        var sorter = new EvenFieldSort<Integer>(reversingStub, x -> x);

        assertElements(sorter.sort(listOf(4, 5, 2, 7, 8, 1, 6), NATURAL),
                6, 5, 8, 7, 2, 1, 4);
    }

    @Test
    void delegateNotCalledWhenNothingToSort() {
        boolean[] called = {false};
        SortStrategy<Integer> spy = (list, comparator) -> { called[0] = true; return list; };
        var sorter = new EvenFieldSort<Integer>(spy, x -> x);

        sorter.sort(listOf(1, 3, 5), NATURAL);
        assertFalse(called[0], "делегат не должен вызываться для всего нечетного списка");

        called[0] = false;
        sorter.sort(listOf(1, 2, 3), NATURAL);
        assertFalse(called[0], "делегат не должен вызываться для одного четного элемента");
    }

    @Test
    void delegateCalledWhenAtLeastTwoEvens() {
        boolean[] called = {false};
        SortStrategy<Integer> spy = (list, comparator) -> { called[0] = true; return list; };
        var sorter = new EvenFieldSort<Integer>(spy, x -> x);
        sorter.sort(listOf(2, 1, 4), NATURAL);
        assertTrue(called[0]);
    }

    @Test
    void customFieldFunctionWithOffset() {
        var sorter = new EvenFieldSort<Integer>(new MergeSort<>(), x -> x + 1);
        assertElements(sorter.sort(listOf(3, 4, 1, 2), NATURAL), 1, 4, 3, 2);
    }

    @Test
    void worksWithQuickSortDelegate() {
        assertElements(new EvenFieldSort<Integer>(new QuickSort<>(), x -> x)
                        .sort(listOf(4, 5, 2, 7, 8, 1, 6), NATURAL),
                2, 5, 4, 7, 6, 1, 8);
    }

    @Test
    void worksWithMergeSortDelegate() {
        assertElements(new EvenFieldSort<Integer>(new MergeSort<>(), x -> x)
                        .sort(listOf(4, 5, 2, 7, 8, 1, 6), NATURAL),
                2, 5, 4, 7, 6, 1, 8);
    }
}
