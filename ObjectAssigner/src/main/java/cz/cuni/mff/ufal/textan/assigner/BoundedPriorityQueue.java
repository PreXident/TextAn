package cz.cuni.mff.ufal.textan.assigner;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Simple bounded priority queue for easy extracting of topK elements.
 * @param <T> element type
 */
public class BoundedPriorityQueue<T> {

    /** Maximal size of the queue. */
    protected final int maxSize;

    /** Inner wrapped queue doing all the work. */
    protected final PriorityQueue<T> queue;

    /**
     * Only constructor.
     * @param maxSize maximal size of the queue
     * @param comparator comparator to use
     */
    public BoundedPriorityQueue(final int maxSize, final Comparator<T> comparator) {
        this.maxSize = maxSize;
        queue = new PriorityQueue<>(maxSize, comparator);
    }

    /**
     * Adds item to the queue.
     * Minimal element is discarded if maxSize is reached.
     * @param item element to add
     * @return true if collection is changed, false otherwise
     */
    public boolean add(final T item) {
        if (queue.size() < maxSize) {
            return queue.add(item);
        }
        if (queue.comparator().compare(queue.peek(), item) < 0) {
            queue.poll();
            return queue.add(item);
        } else {
            return false;
        }
    }

    /**
     * Returns the underlying collection, do not change!
     * @return the underlying collection
     */
    public Collection<T> getQueue() {
        return queue;
    }
}
