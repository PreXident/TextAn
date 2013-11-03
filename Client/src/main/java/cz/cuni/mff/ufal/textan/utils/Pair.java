package cz.cuni.mff.ufal.autopolan.utils;

//this class can be serialized
import java.io.Serializable;

/**
 * Simple generic class to encapsulate two objects.
 * @param <First> type of the first object of the pair
 * @param <Second> type of the second object of the pair
 */
public class Pair<First, Second> implements Cloneable, Serializable {

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
            Pair<First, Second> clone = new Pair<>(firstClone, secondClone);
            return clone;
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Not both of the encapsulated objects are Cloneable!", e);
        } catch (Exception e) {
            throw new UnsupportedOperationException("An error occured!", e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair p = (Pair) obj;
        if (first == null) {
            return p.first == null;
        }
        if (second == null) {
            return p.second == null;
        }
        return first.equals(p.first) && second.equals(p.second);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 59 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
