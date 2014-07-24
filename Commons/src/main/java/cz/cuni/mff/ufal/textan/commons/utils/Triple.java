package cz.cuni.mff.ufal.textan.commons.utils;

//this class can be serialized

import java.io.Serializable;

/**
 * Simple generic class to encapsulate three objects.
 * @param <First> type of the first object of the triple
 * @param <Second> type of the second object of the triple
 * @param <Third> type of the third object of the triple
 */
public class Triple<First, Second, Third> implements Cloneable, Serializable {

    private static final long serialVersionUID = 5400612335228835682L;

    /** First encapsulated object */
    private final First first;

    /** Second encapsulated object */
    private final Second second;

    /** Second encapsulated object */
    private final Third third;

    /**
     * Parameterless constructor of Triple class, both fields will be null.
     */
    public Triple() {
        first = null;
        second = null;
        third = null;
    }

    /**
     * Usual constructor of Triple class.
     * @param first first object to encapsulate
     * @param second second object to encapsulate
     * @param third third object to encapsulate
     */
    public Triple(final First first, final Second second, final Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
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
     * Returns third encapsulated object.
     * @return third encapsulated object
     */
    public Third getThird() {
        return third;
    }

    /**
     * Clones the Triple&lt;First, Second, Third&gt;.
     * @throws UnsupportedOperationException if cloning cannot be performed
     * @return clone of the Triple&lt;First, Second, Third&gt;
     */
    @Override
    @SuppressWarnings("unchecked")
    public Triple<First, Second, Third> clone() {
        if (!(first instanceof Cloneable && second instanceof Cloneable
                && third instanceof Cloneable)) {
            throw new UnsupportedOperationException("Not all of the encapsulated objects are Cloneable!");
        }
        try {
            First firstClone = first == null ? null : (First) first.getClass().getMethod("clone").invoke(first);
            Second secondClone = second == null ? null : (Second) second.getClass().getMethod("clone").invoke(second);
            Third thirdClone = third == null ? null : (Third) third.getClass().getMethod("clone").invoke(third);
            return new Triple<>(firstClone, secondClone, thirdClone);
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
        if (!(obj instanceof Triple)) {
            return false;
        }
        final Triple p = (Triple) obj;
        return equals(first, p.first) && equals(second, p.second)
                && equals(third, p.third);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 59 * hash + (this.second != null ? this.second.hashCode() : 0);
        hash = 59 * hash + (this.third != null ? this.third.hashCode() : 0);
        return hash;
    }
}
