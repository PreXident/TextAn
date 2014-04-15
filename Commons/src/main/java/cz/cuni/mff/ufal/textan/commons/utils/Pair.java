package cz.cuni.mff.ufal.textan.commons.utils;

//this class can be serialized

import java.io.Serializable;

/**
 * Simple generic class to encapsulate two objects.
 * @param <First> type of the first object of the pair
 * @param <Second> type of the second object of the pair
 */
public class Pair<First, Second> implements Cloneable, Serializable {

    private static final long serialVersionUID = -1784712946550240078L;

    /** First encapsulated object */
    private final First first;

    /** Second encapsulated object */
    private final Second second;

    /**
     * Parameterless constructor of Pair class, both fields will be null.
     */
    public Pair() {
        first = null;
        second = null;
    }

    /**
     * Usual constructor of Pair class.
     * @param first first object to encapsulate
     * @param second second object to encapsulate
     */
    public Pair(final First first, final Second second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns first encapsulated object.
     * @return first encapsulated object
     */
    public First getFirst() {
        return first;
    }

    /**
     * Returns second encapsulated object.
     * @return second encapsulated object
     */
    public Second getSecond() {
        return second;
    }

    /**
     * Clones the Pair&lt;First, Second&gt;.
     * @throws UnsupportedOperationException if cloning cannot be performed
     * @return clone of the Pair&lt;First, Second&gt;
     */
    @Override
    @SuppressWarnings("unchecked")
    public Pair<First, Second> clone() {
        if (!(first instanceof Cloneable && second instanceof Cloneable)) {
            throw new UnsupportedOperationException("Not both of the encapsulated objects are Cloneable!");
        }
        try {
            First firstClone = first == null ? null : (First) first.getClass().getMethod("clone").invoke(first);
            Second secondClone = second == null ? null : (Second) second.getClass().getMethod("clone").invoke(second);
            return new Pair<>(firstClone, secondClone);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Not both of the encapsulated objects are Cloneable!", e);
        } catch (Exception e) {
            throw new UnsupportedOperationException("An error occurred!", e);
        }
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
        if (!(obj instanceof Pair)) {
            return false;
        }
        final Pair p = (Pair) obj;
        return equals(first, p.first) && equals(second, p.second);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 59 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
