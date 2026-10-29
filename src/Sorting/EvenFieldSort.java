package Sorting;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class EvenFieldSort<T> implements SortStrategy<T> {
    private final SortStrategy<T> strategy;
    private final ToIntFunction<T> func;

    public EvenFieldSort(SortStrategy<T> strategy, ToIntFunction<T> func) {
        this.strategy = strategy;
        this.func = func;
    }

    @Override
    public SortableList<T> sort(SortableList<T> list, Comparator<T> comparator){
        SortableList<T> workList = new SortableList<T>();
        for(int i = 0; i < list.size(); i++){
            var el = list.get(i);
            if ((func.applyAsInt(el) & 1) == 0)
                workList.add(el);
        }
        workList = strategy.sort(workList, comparator);
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
