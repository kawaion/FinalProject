package test.sorters;

import collection.SortableList;
import org.junit.jupiter.api.Test;
import sorters.SortStrategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractSortStrategyTest {

    protected abstract SortStrategy<Integer> createSorter();

    protected static final Comparator<Integer> NATURAL = Comparator.naturalOrder();
    protected static final Comparator<Integer> REVERSE = Comparator.reverseOrder();

    protected static SortableList<Integer> listOf(Integer... values) {
        SortableList<Integer> list = new SortableList<>();
        for (Integer v : values) list.add(v);
        return list;
    }

    protected static void assertElements(SortableList<Integer> list, Integer... expected) {
        assertEquals(expected.length, list.size(), "размеры не совпадают");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], list.get(i), "расхождение в индексе " + i);
        }
    }

    @Test
    void emptyList() {
        SortableList<Integer> list = listOf();
        SortableList<Integer> result = createSorter().sort(list, NATURAL);
        assertSame(list, result, "сортировка должна возвращать один и тот же экземпляр");
        assertEquals(0, result.size());
    }

    @Test
    void singleElement() {
        assertElements(createSorter().sort(listOf(42), NATURAL), 42);
    }

    @Test
    void twoElementsInOrder() {
        assertElements(createSorter().sort(listOf(1, 2), NATURAL), 1, 2);
    }

    @Test
    void twoElementsOutOfOrder() {
        assertElements(createSorter().sort(listOf(2, 1), NATURAL), 1, 2);
    }

    @Test
    void alreadySorted() {
        assertElements(createSorter().sort(listOf(1, 2, 3, 4, 5), NATURAL),
                1, 2, 3, 4, 5);
    }

    @Test
    void reverseSorted() {
        assertElements(createSorter().sort(listOf(5, 4, 3, 2, 1), NATURAL),
                1, 2, 3, 4, 5);
    }

    @Test
    void duplicates() {
        assertElements(createSorter().sort(listOf(3, 1, 2, 3, 1, 2, 1), NATURAL),
                1, 1, 1, 2, 2, 3, 3);
    }

    @Test
    void allEqual() {
        assertElements(createSorter().sort(listOf(7, 7, 7, 7), NATURAL),
                7, 7, 7, 7);
    }

    @Test
    void negativeNumbers() {
        assertElements(createSorter().sort(listOf(-3, 5, -10, 0, 7, -1), NATURAL),
                -10, -3, -1, 0, 5, 7);
    }

    @Test
    void reverseComparator() {
        assertElements(createSorter().sort(listOf(5, 3, 8, 1, 9), REVERSE),
                9, 8, 5, 3, 1);
    }

    @Test
    void returnsSameInstance() {
        SortableList<Integer> list = listOf(3, 1, 2);
        assertSame(list, createSorter().sort(list, NATURAL));
    }

    @Test
    void randomData200Elements() {
        Random r = new Random(42);
        Integer[] input = new Integer[200];
        for (int i = 0; i < input.length; i++) input[i] = r.nextInt(200) - 100;
        Integer[] expected = input.clone();
        Arrays.sort(expected);

        assertElements(createSorter().sort(listOf(input), NATURAL), expected);
    }

    @Test
    void largePowerOfTwoSize() {
        Random r = new Random(7);
        Integer[] input = new Integer[128];
        for (int i = 0; i < input.length; i++) input[i] = r.nextInt(1000);
        Integer[] expected = input.clone();
        Arrays.sort(expected);
        assertElements(createSorter().sort(listOf(input), NATURAL), expected);
    }

    @Test
    void oddSizedRandom() {
        Random r = new Random(11);
        Integer[] input = new Integer[77];
        for (int i = 0; i < input.length; i++) input[i] = r.nextInt(50);
        Integer[] expected = input.clone();
        Arrays.sort(expected);
        assertElements(createSorter().sort(listOf(input), NATURAL), expected);
    }
}
