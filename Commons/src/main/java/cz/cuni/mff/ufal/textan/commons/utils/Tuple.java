package cz.cuni.mff.ufal.textan.commons.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Experimental class generalizing Pair.
 */
public class Tuple implements Cloneable, Serializable {

    private static final long serialVersionUID = -881745874207797935L;

    /** Encapsulated objects. */
    private final List<Object> list;

    /**
     * Creates empty tuple with length 0.
     */
    public Tuple() {
        list = Collections.emptyList();
    }

    /**
     * Creates empty tuple (filled with nulls) with given size
     * @param size
     */
    public Tuple(int size) {
        list = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            list.add(null);
        }
    }

    /**
     * Created tuple with given content.
     * @param objects tuple content
     */
    public Tuple(Object... objects) {
        this(Arrays.asList(objects));
    }

    /**
     * Created tuple with given content.
     * @param objects tuple content
     */
    public Tuple(Collection<Object> objects) {
        list = new ArrayList<>(objects);
    }

    /**
     * Clones the Tuple.
     * @throws UnsupportedOperationException if cloning cannot be performed
     * @return clone of the Tuple
     */
    @Override
    @SuppressWarnings("unchecked")
    public Tuple clone() {
        final List<Object> l = new ArrayList<>();
        for (Object obj : list) {
            if (obj != null && !(obj instanceof Cloneable)) {
                throw new UnsupportedOperationException("Not all of the encapsulated objects are Cloneable!");
            }
            try {
                final Object clone = obj == null ? null : obj.getClass().getMethod("clone").invoke(obj);
                l.add(clone);
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("Not all of the encapsulated objects are Cloneable!", e);
            } catch (Exception e) {
                throw new UnsupportedOperationException("An error occurred!", e);
            }
        }
        return new Tuple(l);
    }

    /**
     * Returns whether the objects are equal.
     * @param obj1 first object to test
     * @param obj2 second object to test
     * @return true if the objects are equal, false otherwise
     */
    private boolean equals(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        } else {
            return obj1.equals(obj2);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        final Tuple t = (Tuple) obj;
        if (t.list.size() != list.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (!equals(list.get(0), t.list.get(0))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns object on index i typed to T.
     * @param <T> type of i-th object
     * @param i object index
     * @return object on index i typed to T
     */
    @SuppressWarnings("unchecked")
    public <T> T get(int i) {
        return (T) list.get(i);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        for (Object obj : list) {
            hash = 59 * hash + (obj != null ? obj.hashCode() : 0);
        }
        return hash;
    }
}
