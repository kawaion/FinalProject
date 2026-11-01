package Sorters;

import collection.CustomList;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class EvenFieldSort<E> implements Sorter<E> {
    private final Sorter<E> sorter;
    private final ToIntFunction<E> func;

    public EvenFieldSort(Sorter<E> sorter, ToIntFunction<E> func) {
        this.sorter = sorter;
        this.func = func;
    }

    @Override
    public CustomList<E> sort(CustomList<E> list, Comparator<E> comparator){
        CustomList<E> workList = new CustomList<E>();
        for(int i = 0; i < list.size(); i++){
            var el = list.get(i);
            if ((func.applyAsInt(el) & 1) == 0)
                workList.add(el);
        }

        if(workList.size() <= 1)
            return list;

        workList = sorter.sort(workList, comparator);
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
