package test.sorter;

import collection.SortableList;
import org.junit.jupiter.api.Test;
import sorter.MergeSort;
import sorter.SortStrategy;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest extends AbstractSortStrategyTest {

    @Override
    protected SortStrategy<Integer> createSorter() {
        return new MergeSort<>();
    }

    @Override
    protected SortStrategy<String> createStringSorter() {return new MergeSort<>();}

    record Item(int key, int id) {}

    @Test
    void isStableOnEqualKeys() {
        SortableList<Item> list = new SortableList<>();
        list.add(new Item(2, 0));
        list.add(new Item(1, 1));
        list.add(new Item(2, 2));
        list.add(new Item(1, 3));
        list.add(new Item(2, 4));
        list.add(new Item(1, 5));

        Comparator<Item> byKey = Comparator.comparingInt(Item::key);
        new MergeSort<Item>().sort(list, byKey);

        int[] ids = new int[list.size()];
        for (int i = 0; i < ids.length; i++) ids[i] = list.get(i).id();
        assertArrayEquals(new int[]{1, 3, 5, 0, 2, 4}, ids);
    }

    @Test
    void stabilityWhenAllKeysEqual() {
        record Wrapped(int key, String tag) {}
        SortableList<Wrapped> list = new SortableList<>();
        list.add(new Wrapped(5, "a"));
        list.add(new Wrapped(5, "b"));
        list.add(new Wrapped(5, "c"));
        list.add(new Wrapped(5, "d"));

        new MergeSort<Wrapped>().sort(list, Comparator.comparingInt(Wrapped::key));

        assertEquals("a", list.get(0).tag());
        assertEquals("b", list.get(1).tag());
        assertEquals("c", list.get(2).tag());
        assertEquals("d", list.get(3).tag());
    }
}
