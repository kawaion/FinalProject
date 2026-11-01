package Sorters;

import collection.CustomList;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

public class MergeSort<E> implements Sorter<E> {
    private CustomList<E> worklist;
    private Comparator<E> cmp;

    @Override
    public CustomList<E> sort(CustomList<E> list, Comparator<E> comparator) {
        this.worklist = list;
        this.cmp = comparator;
        sortRange(new ImageSubList(0, list.size()));
        return list;
    }

    private ImageSubList sortRange(ImageSubList range) {
        if (range.size() <= 1) return range;

        int half = range.size() / 2;
        var left  = new ImageSubList(range.left(), range.left() + half);
        var right = new ImageSubList(range.left() + half, range.right());

        left = sortRange(left);
        right = sortRange(right);
        return merge(left, right);
    }

    private ImageSubList merge(ImageSubList a, ImageSubList b) {
        int na = a.size(), nb = b.size();
        Deque<E> buf = new ArrayDeque<>(na + nb);

        int i = 0, j = 0;
        while (i < na && j < nb) {
            if (cmp.compare(a.get(i), b.get(j)) <= 0)
            {
                buf.add(a.get(i));
                i++;
            }
            else
            {
                buf.add(b.get(j));
                j++;
            }
        }
        while (i < na){
            buf.add(a.get(i));
            i++;
        }
        while (j < nb) {
            buf.add(b.get(j));
            j++;
        }

        var res = new ImageSubList(a.left(), b.right());
        for (int k = 0; k < res.size(); k++) res.set(k, buf.pop());
        return res;
    }

    private final class ImageSubList {
        private final int offset;
        private final int count;

        ImageSubList(int from, int to) {
            if (from < 0 || to < from || to > worklist.size())
                throw new IndexOutOfBoundsException(from + ".." + to);
            this.offset = from;
            this.count = to - from;
        }

        int left()  { return offset; }
        int right() { return offset + count; }
        int size()  { return count; }

        E get(int i) {
            if (i < 0 || i >= count) throw new IndexOutOfBoundsException(i);
            return worklist.get(offset + i);
        }

        void set(int i, E v) {
            if (i < 0 || i >= count) throw new IndexOutOfBoundsException(i);
            worklist.set(offset + i, v);
        }
    }
}
