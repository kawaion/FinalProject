package test.sorters;

import collection.SortableList;
import org.junit.jupiter.api.Test;
import sorters.SortStrategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractStringSortStrategyTest {

    protected abstract SortStrategy<String> createStringSorter();

    protected static final Comparator<String> ALPHABETICAL = String.CASE_INSENSITIVE_ORDER;
    protected static final Comparator<String> ALPHABETICAL_DESC = ALPHABETICAL.reversed();
    protected static final Comparator<String> ALPHABETICAL_TOTAL = ALPHABETICAL.thenComparing(Comparator.naturalOrder());

    protected static SortableList<String> listOfStrings(String... values) {
        SortableList<String> list = new SortableList<>();
        for (String v : values) list.add(v);
        return list;
    }

    protected static void assertStrings(SortableList<String> list, String... expected) {
        assertEquals(expected.length, list.size(), "размеры не совпадают");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], list.get(i), "расхождение в индексе " + i);
        }
    }

    @Test
    void stringEmptyList() {
        SortableList<String> list = listOfStrings();
        SortableList<String> result = createStringSorter().sort(list, ALPHABETICAL);
        assertSame(list, result, "сортировка должна возвращать один и тот же экземпляр");
        assertEquals(0, result.size());
    }

    @Test
    void stringSingleElement() {
        assertStrings(createStringSorter().sort(listOfStrings("only"), ALPHABETICAL), "only");
    }

    @Test
    void stringTwoInOrder() {
        assertStrings(
                createStringSorter().sort(listOfStrings("apple", "banana"), ALPHABETICAL),
                "apple", "banana");
    }

    @Test
    void stringTwoOutOfOrder() {
        assertStrings(
                createStringSorter().sort(listOfStrings("banana", "apple"), ALPHABETICAL),
                "apple", "banana");
    }

    @Test
    void stringAlreadySorted() {
        assertStrings(
                createStringSorter().sort(
                        listOfStrings("apple", "banana", "cherry", "date"), ALPHABETICAL),
                "apple", "banana", "cherry", "date");
    }

    @Test
    void stringReverseSorted() {
        assertStrings(
                createStringSorter().sort(
                        listOfStrings("date", "cherry", "banana", "apple"), ALPHABETICAL),
                "apple", "banana", "cherry", "date");
    }

    @Test
    void stringMixedCaseAlphabetical() {
        // регистронезависимый порядок: Apple → apricot → banana → cherry
        assertStrings(
                createStringSorter().sort(
                        listOfStrings("banana", "Apple", "cherry", "apricot"), ALPHABETICAL),
                "Apple", "apricot", "banana", "cherry");
    }

    @Test
    void stringReverseAlphabetical() {
        assertStrings(
                createStringSorter().sort(
                        listOfStrings("banana", "Apple", "cherry", "apricot"),
                        ALPHABETICAL_DESC),
                "cherry", "banana", "apricot", "Apple");
    }

    @Test
    void stringDuplicates() {
        assertStrings(
                createStringSorter().sort(
                        listOfStrings("b", "a", "b", "a", "c", "b"), ALPHABETICAL),
                "a", "a", "b", "b", "b", "c");
    }

    @Test
    void stringAllEqual() {
        assertStrings(
                createStringSorter().sort(listOfStrings("x", "x", "x", "x"), ALPHABETICAL),
                "x", "x", "x", "x");
    }

    @Test
    void stringEmptyStringsSortedFirst() {
        assertStrings(
                createStringSorter().sort(listOfStrings("b", "", "a", ""), ALPHABETICAL),
                "", "", "a", "b");
    }

    @Test
    void stringReturnsSameInstance() {
        SortableList<String> list = listOfStrings("banana", "apple");
        assertSame(list, createStringSorter().sort(list, ALPHABETICAL));
    }

    @Test
    void stringRandomMixedCase() {
        Random r = new Random(2024);
        String[] input = new String[150];
        for (int i = 0; i < input.length; i++) {
            int len = 1 + r.nextInt(6);
            char[] buf = new char[len];
            for (int k = 0; k < len; k++) {
                char c = (char) ('a' + r.nextInt(26));
                if (r.nextBoolean()) c = Character.toUpperCase(c);
                buf[k] = c;
            }
            input[i] = new String(buf);
        }
        String[] expected = input.clone();
        Arrays.sort(expected, ALPHABETICAL_TOTAL);

        assertStrings(createStringSorter().sort(listOfStrings(input), ALPHABETICAL_TOTAL), expected);
    }

    @Test
    void stringSortedInvariant() {
        SortableList<String> list = listOfStrings(
                "delta", "Alpha", "charlie", "BRAVO", "echo", "apricot");
        createStringSorter().sort(list, ALPHABETICAL);
        for (int i = 1; i < list.size(); i++) {
            assertTrue(ALPHABETICAL.compare(list.get(i - 1), list.get(i)) <= 0,
                    "не по алфавиту в индексе " + i
                            + ": " + list.get(i - 1) + " > " + list.get(i));
        }
    }
}