package test;

import collection.SortableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sorters.EvenFieldSort;
import sorters.MergeSort;
import sorters.QuickSort;
import sorters.SortStrategy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortableListTest {

    @Test
    @DisplayName("Новый список пуст")
    void newListIsEmpty() {
        SortableList<String> list = new SortableList<>();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("После добавления список перестаёт быть пустым")
    void listIsNotEmptyAfterAdd() {
        SortableList<String> list = new SortableList<>();

        list.add("value");

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("После удаления всех элементов список снова пуст")
    void listIsEmptyAfterRemovingAllElements() {
        SortableList<String> list = new SortableList<>();
        list.add("one");
        list.add("two");

        list.remove(0);
        list.remove(0);

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }


    @Test
    @DisplayName("add(E) добавляет элементы в конец и возвращает true")
    void addAppendsElementsAndReturnsTrue() {
        SortableList<String> list = new SortableList<>();

        assertTrue(list.add("first"));
        assertTrue(list.add("second"));
        assertTrue(list.add(null));

        assertEquals(3, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
        assertEquals(null, list.get(2));
    }

    @Test
    @DisplayName("Список сохраняет порядок добавления элементов")
    void getReturnsElementsInInsertionOrder() {
        SortableList<Integer> list = new SortableList<>();
        List<Integer> expected = List.of(10, 20, 30, 40, 50);

        for (Integer value : expected) {
            list.add(value);
        }

        assertEquals(expected, list.stream().toList());
    }

    @Test
    @DisplayName("Внутренний массив увеличивается при переполнении")
    void growsWhenAddingMoreThanDefaultCapacity() {
        SortableList<Integer> list = new SortableList<>();
        int count = 100;

        for (int i = 0; i < count; i++) {
            list.add(i);
        }

        assertEquals(count, list.size());
        for (int i = 0; i < count; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    @DisplayName("Рост массива не затрагивает уже добавленные элементы")
    void growthKeepsExistingElements() {
        SortableList<String> list = new SortableList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        for (int i = 0; i < 50; i++) {
            list.add("value-" + i);
        }

        assertEquals(53, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        assertEquals("value-49", list.get(52));
    }


    @Test
    @DisplayName("set заменяет элемент и возвращает прежнее значение")
    void setReplacesElementAndReturnsPreviousValue() {
        SortableList<String> list = new SortableList<>();
        list.add("old");

        String previous = list.set(0, "new");

        assertEquals("old", previous);
        assertEquals("new", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("set не меняет размер списка")
    void setDoesNotChangeSize() {
        SortableList<Integer> list = new SortableList<>();
        list.add(1);
        list.add(2);

        list.set(1, 20);

        assertEquals(2, list.size());
        assertEquals(List.of(1, 20), list.stream().toList());
    }


    @Test
    @DisplayName("add(index, E) вставляет элемент и сдвигает остальные")
    void addAtIndexInsertsElementAndShiftsOthers() {
        SortableList<String> list = new SortableList<>();
        list.add("first");
        list.add("third");

        list.add(1, "second");

        assertEquals(3, list.size());
        assertEquals(List.of("first", "second", "third"), list.stream().toList());
    }

    @Test
    @DisplayName("add(0, E) вставляет элемент в начало")
    void addAtStartInsertsElementAtBeginning() {
        SortableList<String> list = new SortableList<>();
        list.add("second");
        list.add("third");

        list.add(0, "first");

        assertEquals(List.of("first", "second", "third"), list.stream().toList());
    }

    @Test
    @DisplayName("add(size(), E) добавляет элемент в конец")
    void addAtEndIsAllowed() {
        SortableList<String> list = new SortableList<>();
        list.add("first");

        list.add(1, "second");

        assertEquals(2, list.size());
        assertEquals(List.of("first", "second"), list.stream().toList());
    }

    @Test
    @DisplayName("add(0, E) в пустой список допустим")
    void addAtIndexZeroToEmptyListIsAllowed() {
        SortableList<String> list = new SortableList<>();

        list.add(0, "only");

        assertEquals(1, list.size());
        assertEquals("only", list.get(0));
    }

    @Test
    @DisplayName("Вставка в середину при переполнении корректно расширяет массив")
    void addAtIndexTriggersGrowthAndKeepsOrder() {
        SortableList<Integer> list = new SortableList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i);
        }

        list.add(4, 100);

        assertEquals(9, list.size());
        assertEquals(List.of(0, 1, 2, 3, 100, 4, 5, 6, 7), list.stream().toList());
    }

  

    @Test
    @DisplayName("remove удаляет первый элемент и возвращает его")
    void removeFirstElement() {
        SortableList<String> list = new SortableList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        String removed = list.remove(0);

        assertEquals("first", removed);
        assertEquals(2, list.size());
        assertEquals(List.of("second", "third"), list.stream().toList());
    }

    @Test
    @DisplayName("remove удаляет элемент из середины и сдвигает остальные")
    void removeMiddleElement() {
        SortableList<String> list = new SortableList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        String removed = list.remove(1);

        assertEquals("second", removed);
        assertEquals(2, list.size());
        assertEquals(List.of("first", "third"), list.stream().toList());
    }

    @Test
    @DisplayName("remove удаляет последний элемент")
    void removeLastElement() {
        SortableList<String> list = new SortableList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        String removed = list.remove(2);

        assertEquals("third", removed);
        assertEquals(2, list.size());
        assertEquals(List.of("first", "second"), list.stream().toList());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    @DisplayName("remove из единственного элемента очищает список")
    void removeFromSingleElementList() {
        SortableList<String> list = new SortableList<>();
        list.add("only");

        String removed = list.remove(0);

        assertEquals("only", removed);
        assertTrue(list.isEmpty());
    }


    @Test
    @DisplayName("clear очищает список")
    void clearMakesListEmpty() {
        SortableList<String> list = new SortableList<>();
        list.add("one");
        list.add("two");

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    @DisplayName("После clear список можно использовать повторно")
    void listIsReusableAfterClear() {
        SortableList<Integer> list = new SortableList<>();
        list.add(1);
        list.add(2);

        list.clear();
        list.add(42);

        assertEquals(1, list.size());
        assertEquals(42, list.get(0));
    }


    @Test
    @DisplayName("sort сортирует элементы по естественному порядку")
    void sortOrdersElementsUsingNaturalComparator() {
        SortableList<Integer> list = new SortableList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        list.sort(Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3), list.stream().toList());
    }

    @Test
    @DisplayName("sort учитывает переданный компаратор")
    void sortUsesProvidedComparator() {
        SortableList<Integer> list = new SortableList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        list.sort(Comparator.reverseOrder());

        assertEquals(List.of(3, 2, 1), list.stream().toList());
    }

    @Test
    @DisplayName("sort упорядочивает строки")
    void sortOrdersStrings() {
        SortableList<String> list = new SortableList<>();
        list.add("banana");
        list.add("apple");
        list.add("cherry");

        list.sort(Comparator.naturalOrder());

        assertEquals(List.of("apple", "banana", "cherry"), list.stream().toList());
    }

    @Test
    @DisplayName("sort пустого списка ничего не делает")
    void sortEmptyListIsNoOp() {
        SortableList<Integer> list = new SortableList<>();

        list.sort(Comparator.naturalOrder());

        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("sort списка из одного элемента ничего не меняет")
    void sortSingleElementListIsNoOp() {
        SortableList<Integer> list = new SortableList<>();
        list.add(42);

        list.sort(Comparator.naturalOrder());

        assertEquals(1, list.size());
        assertEquals(42, list.get(0));
    }

    @Test
    @DisplayName("sort сохраняет все элементы")
    void sortKeepsAllElements() {
        SortableList<Integer> list = new SortableList<>();
        List<Integer> values = List.of(5, 1, 4, 2, 3);
        for (Integer value : values) {
            list.add(value);
        }

        list.sort(Comparator.naturalOrder());

        assertEquals(values.size(), list.size());
        assertEquals(List.of(1, 2, 3, 4, 5), list.stream().toList());
    }


    @Test
    @DisplayName("customSort использует QuickSort")
    void customSortUsesQuickSort() {
        SortableList<Integer> list = new SortableList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        list.customSort(new QuickSort<>(), Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3), list.stream().toList());
    }

    @Test
    @DisplayName("customSort использует MergeSort")
    void customSortUsesMergeSort() {
        SortableList<Integer> list = new SortableList<>();
        list.add(5);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);

        list.customSort(new MergeSort<>(), Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3, 4, 5), list.stream().toList());
    }

    @Test
    @DisplayName("customSort с QuickSort сортирует по убыванию")
    void customSortSupportsReverseComparator() {
        SortableList<Integer> list = new SortableList<>();
        list.add(2);
        list.add(5);
        list.add(1);
        list.add(4);
        list.add(3);

        list.customSort(new QuickSort<>(), Comparator.reverseOrder());

        assertEquals(List.of(5, 4, 3, 2, 1), list.stream().toList());
    }

    @Test
    @DisplayName("customSort с MergeSort работает на пустом списке и списке из одного элемента")
    void customSortHandlesSmallLists() {
        SortableList<Integer> empty = new SortableList<>();
        empty.customSort(new MergeSort<>(), Comparator.naturalOrder());
        assertTrue(empty.isEmpty());

        SortableList<Integer> single = new SortableList<>();
        single.add(7);
        single.customSort(new MergeSort<>(), Comparator.naturalOrder());
        assertEquals(List.of(7), single.stream().toList());
    }

    @Test
    @DisplayName("EvenFieldSort сортирует только элементы с чётным значением поля")
    void evenFieldSortSortsOnlyEvenValues() {
        SortableList<Integer> list = new SortableList<>();
        List<Integer> source = List.of(5, 3, 8, 1, 4, 2, 7, 6);
        for (Integer value : source) {
            list.add(value);
        }

        SortStrategy<Integer> strategy = new EvenFieldSort<>(new QuickSort<>(), value -> value);
        list.customSort(strategy, Comparator.naturalOrder());

        assertEquals(List.of(5, 3, 2, 1, 4, 6, 7, 8), list.stream().toList());
    }

    @Test
    @DisplayName("customSort принимает лямбда-реализацию SortStrategy")
    void customSortAcceptsLambdaStrategy() {
        SortableList<Integer> list = new SortableList<>();
        list.add(3);
        list.add(1);
        list.add(2);

        SortStrategy<Integer> strategy = (items, comparator) -> {
            items.sort(comparator);
            return items;
        };

        list.customSort(strategy, Comparator.naturalOrder());

        assertEquals(List.of(1, 2, 3), list.stream().toList());
    }


    @Test
    @DisplayName("stream возвращает элементы в порядке списка")
    void streamReturnsElementsInOrder() {
        SortableList<String> list = new SortableList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(List.of("a", "b", "c"), list.stream().toList());
    }

    @Test
    @DisplayName("stream пустого списка не содержит элементов")
    void streamOfEmptyListIsEmpty() {
        SortableList<String> list = new SortableList<>();

        assertEquals(0, list.stream().count());
    }

    @Test
    @DisplayName("stream можно использовать для фильтрации и преобразования")
    void streamSupportsIntermediateOperations() {
        SortableList<Integer> list = new SortableList<>();
        for (int i = 1; i <= 5; i++) {
            list.add(i);
        }

        List<Integer> result = list.stream()
                .filter(value -> value % 2 == 0)
                .map(value -> value * 10)
                .toList();

        assertEquals(List.of(20, 40), result);
    }

    @Test
    @DisplayName("stream не изменяет исходный список")
    void streamDoesNotModifyList() {
        SortableList<Integer> list = new SortableList<>();
        list.add(2);
        list.add(1);

        list.stream().sorted().toList();

        assertEquals(List.of(2, 1), list.stream().toList());
    }

   

    @Test
    @DisplayName("Коллектор toSortableList собирает элементы последовательного потока")
    void toSortableListCollectorCollectsElements() {
        SortableList<Integer> result = Stream.of(1, 2, 3)
                .collect(SortableList.toSortableList());

        assertEquals(3, result.size());
        assertEquals(List.of(1, 2, 3), result.stream().toList());
    }

    @Test
    @DisplayName("Коллектор toSortableList работает с пустым потоком")
    void collectorWorksWithEmptyStream() {
        SortableList<Integer> result = Stream.<Integer>empty()
                .collect(SortableList.toSortableList());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Коллектор toSortableList корректно объединяет части параллельного потока")
    void collectorWorksWithParallelStream() {
        List<Integer> source = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        SortableList<Integer> result = source.parallelStream()
                .collect(SortableList.toSortableList());

        assertEquals(source.size(), result.size());
        assertEquals(new HashSet<>(source), new HashSet<>(result.stream().toList()));
    }

    @Test
    @DisplayName("Коллектор toSortableList не теряет и не дублирует элементы")
    void collectorKeepsAllElementsWithoutDuplicates() {
        Set<Integer> expected = Set.of(1, 2, 3, 4, 5);

        SortableList<Integer> result = Stream.of(1, 2, 3, 4, 5)
                .parallel()
                .collect(SortableList.toSortableList());

        assertEquals(5, result.size());
        assertEquals(expected, new HashSet<>(result.stream().toList()));
    }



    @Test
    @DisplayName("get выбрасывает IndexOutOfBoundsException для недопустимого индекса")
    void getThrowsForInvalidIndex() {
        SortableList<String> list = new SortableList<>();
        list.add("value");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()));
    }

    @Test
    @DisplayName("get пустого списка выбрасывает IndexOutOfBoundsException")
    void getThrowsForEmptyList() {
        SortableList<String> list = new SortableList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    @DisplayName("set выбрасывает IndexOutOfBoundsException для недопустимого индекса")
    void setThrowsForInvalidIndex() {
        SortableList<String> list = new SortableList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, "value"));

        list.add("first");
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, "value"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "value"));
    }

    @Test
    @DisplayName("add(index, E) выбрасывает IndexOutOfBoundsException для недопустимого индекса")
    void addAtIndexThrowsForInvalidIndex() {
        SortableList<String> list = new SortableList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "value"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, "value"));

        list.add("first");
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, "value"));
    }

    @Test
    @DisplayName("remove выбрасывает IndexOutOfBoundsException для недопустимого индекса")
    void removeThrowsForInvalidIndex() {
        SortableList<String> list = new SortableList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));

        list.add("first");
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    }

    @Test
    @DisplayName("Недопустимый индекс не изменяет список")
    void invalidIndexDoesNotModifyList() {
        SortableList<String> list = new SortableList<>();
        list.add("first");
        list.add("second");

        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, "third"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(2, "third"));

        assertEquals(2, list.size());
        assertEquals(List.of("first", "second"), list.stream().toList());
    }
}
