package cz.cuni.mff.ufal.autopolan.utils;

import javafx.beans.value.WritableValue;

/**
 * Holder for (in)out parameters.
 */
public class Ref<T> implements WritableValue<T> {

    /** Held value. */
    public T val;

    /**
     * Creates Ref to null value.
     */
    public Ref() {
        val = null;
    }

    /**
     * Creates Ref to val.
     * @param val reference value
     */
    public Ref(final T val) {
        this.val = val;
    }

    /**
     * Gets val.
     * @return val
     */
    public T getVal() {
        return val;
    }

    /**
     * Sets val.
     * @param val new value
     */
    public void setVal(final T val) {
        this.val = val;
    }

    /**
     * Sets val.
     * @param val new value
     * @return this
     */
    public Ref<T> withVal(final T val) {
        setVal(val);
        return this;
    }

    /**
     * Gets val.
     * @return val
     */
    @Override
    public T getValue() {
        return getVal();
    }

    /**
     * Sets val.
     * @param value new value
     */
    @Override
    public void setValue(final T value) {
        setVal(value);
    }

    /**
     * Sets val.
     * @param val new value
     * @return this
     */
    public Ref<T> withValue(final T val) {
        return withVal(val);
    }

    @Override
    public boolean equals(final Object obj) {
        Object inner = innerObject();
        if (obj == null) {
            return inner == null;
        } else {
            return obj.equals(inner);
        }
    }

    @Override
    public int hashCode() {
        return val != null ? val.hashCode() : 0;
    }

    /**
     * Returns the inner object in reference hierarchy.
     * @return inner object of reference hierarchy
     */
    private Object innerObject() {
        Object res = val;
        while (res != null && res instanceof Ref) {
            res = ((Ref) res).val;
        }
        return res;
    }
}
