package org.top.collection;

import org.junit.jupiter.api.Test;
import org.top.strategy.Sorter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SortableListTest {

    @Test
    void newListIsEmpty() {
        SortableList list = new SortableList();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addAndGetElements() {
        SortableList list = new SortableList();

        assertTrue(list.add("first"));
        assertTrue(list.add("second"));

        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
    }

    @Test
    void growsWhenAddingMoreThanDefaultCapacity() {
        SortableList list = new SortableList();

        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        assertEquals(20, list.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    void setReplacesElementAndReturnsOldValue() {
        SortableList list = new SortableList();
        list.add("old");

        String previous = list.set(0, "new");

        assertEquals("old", previous);
        assertEquals("new", list.get(0));
    }

    @Test
    void addAtIndexInsertsElementAndShiftsOthers() {
        SortableList list = new SortableList();
        list.add("first");
        list.add("third");

        list.add(1, "second");

        assertEquals(3, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
        assertEquals("third", list.get(2));
    }

    @Test
    void addAtEndIsAllowed() {
        SortableList list = new SortableList();
        list.add("first");

        list.add(1, "second");

        assertEquals(2, list.size());
        assertEquals("second", list.get(1));
    }

    @Test
    void removeReturnsElementAndShiftsOthers() {
        SortableList list = new SortableList();
        list.add("first");
        list.add("second");
        list.add("third");

        String removed = list.remove(1);

        assertEquals("second", removed);
        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("third", list.get(1));
    }

    @Test
    void clearMakesListEmpty() {
        SortableList list = new SortableList();
        list.add("value");

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void sortOrdersElementsUsingComparator() {
        SortableList list = new SortableList();
        list.add(3);
        list.add(1);
        list.add(2);

        list.sort(Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3), list.stream().toList());
    }

    @Test
    void customSortUsesProvidedSorter() {
        SortableList list = new SortableList();
        list.add(3);
        list.add(1);
        list.add(2);

        Sorter sorter = (items, comparator) -> items.sort(comparator);
        list.customSort(sorter, Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3), list.stream().toList());
    }

    @Test
    void streamReturnsElementsInOrder() {
        SortableList list = new SortableList();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(List.of("a", "b", "c"), list.stream().toList());
    }

    @Test
    void toSortableListCollectorCollectsElements() {
        SortableList result = Stream.of(1, 2, 3)
                .collect(SortableList.toSortableList());

        assertEquals(3, result.size());
        assertEquals(List.of(1, 2, 3), result.stream().toList());
    }

    @Test
    void collectorWorksWithParallelStream() {
        SortableList result = Stream.of(1, 2, 3, 4, 5)
                .parallel()
                .collect(SortableList.toSortableList());

        assertEquals(5, result.size());
        assertEquals(List.of(1, 2, 3, 4, 5), result.stream().sorted().toList());
    }

    @Test
    void getThrowsForInvalidIndex() {
        SortableList list = new SortableList();
        list.add("value");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void setThrowsForInvalidIndex() {
        SortableList list = new SortableList();

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, "value"));
    }

    @Test
    void addAtIndexThrowsForInvalidIndex() {
        SortableList list = new SortableList();

        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "value"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, "value"));
    }

    @Test
    void removeThrowsForInvalidIndex() {
        SortableList list = new SortableList();

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }
}