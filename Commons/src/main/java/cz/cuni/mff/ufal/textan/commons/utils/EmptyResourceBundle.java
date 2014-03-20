package cz.cuni.mff.ufal.textan.commons.utils;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * Empty {@link ResourceBundle}. Always returns key back.
 */
public class EmptyResourceBundle extends ResourceBundle {

    @Override
    protected Object handleGetObject(final String key) {
        return key;
    }

    @Override
    public Enumeration<String> getKeys() {
        return new EmptyEnumeration<>();
    }

    /**
     * Empty {@link Enumeration}.
     * @param <T> iterating over T
     */
    static private class EmptyEnumeration<T> implements Enumeration<T> {

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public T nextElement() {
            throw new NoSuchElementException();
        }
    }
}
