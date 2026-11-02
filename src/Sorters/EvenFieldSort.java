package Sorters;

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

    @Override
    public SortableList<E> sort(SortableList<E> list, Comparator<E> comparator){
        SortableList<E> workList = new SortableList<E>();
        for(int i = 0; i < list.size(); i++){
            var el = list.get(i);
            if ((func.applyAsInt(el) & 1) == 0)
                workList.add(el);
        }

        if(workList.size() <= 1)
            return list;

        workList = sortStrategy.sort(workList, comparator);
        int j = 0;
        for(int i = 0; i < list.size(); i++){
            var el = list.get(i);
            if ((func.applyAsInt(el) & 1) == 0){
                list.set(i, workList.get(j));
                j++;
            }
        }
        return list;
    }
}
