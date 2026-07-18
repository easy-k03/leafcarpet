package carpet.utils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class EvictingQueue<E> implements Iterable<E>
{
    private final LinkedHashSet<E> set = new LinkedHashSet<>();
    private final int maxSize;

    public EvictingQueue()
    {
        this(100);
    }

    public EvictingQueue(int maxSize)
    {
        this.maxSize = maxSize;
    }

    public void put(E element)
    {
        if (set.size() >= maxSize)
        {
            Iterator<E> it = set.iterator();
            if (it.hasNext())
            {
                it.next();
                it.remove();
            }
        }
        set.add(element);
    }

    public Set<E> keySet()
    {
        return set;
    }

    public E get(E element)
    {
        for (E e : set)
        {
            if (e.equals(element))
                return e;
        }
        return null;
    }

    @Override
    public Iterator<E> iterator()
    {
        return set.iterator();
    }
}
