package sorter;

import collection.SortableList;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class EvenFieldSort<E> implements SortStrategy<E> {
    private final SortStrategy<E> sortStrategy;
    private final ToIntFunction<E> func;

    public EvenFieldSort(SortStrategy<E> sortStrategy, ToIntFunction<E> func) {
        this.sortStrategy = sortStrategy;
        this.func = func;
    }

    private record EvenSplit<E>(SortableList<Integer> indexList,
                                SortableList<E> workList) {}

    @Override
    public SortableList<E> sort(SortableList<E> list, Comparator<E> comparator){
        var split = extractEvenFieldElements(list, func);

        var workList = split.workList();
        var indexList = split.indexList();

        if(workList.size() <= 1)
            return list;

        workList = sortStrategy.sort(workList, comparator);

        list = writeBackInPlace(list, indexList, workList);

        return list;
    }

    private EvenSplit<E> extractEvenFieldElements(SortableList<E> list, ToIntFunction<E> func) {
        SortableList<E> workList = new SortableList<>();
        SortableList<Integer> indexList = new SortableList<>();
        for(int i = 0; i < list.size(); i++){
            var el = list.get(i);
            if ((func.applyAsInt(el) & 1) == 0){
                indexList.add(i);
                workList.add(el);
            }
        }
        return new EvenSplit<>(indexList, workList);
    }

    private SortableList<E> writeBackInPlace(SortableList<E> list, SortableList<Integer> indexList, SortableList<E> workList) {
        for(int j = 0; j < indexList.size(); j++){
            var el = workList.get(j);
            var i = indexList.get(j);
            list.set(i, el);
        }
        return list;
    }
}
