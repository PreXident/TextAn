/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

/**
 * Superclass of all database textan exceptions
 * 
 * @author Vaclav Pernicka
 */
public abstract class AbstractDatabaseTextanException extends Exception {
    private static final long serialVersionUID = 30001;
    Object tag = null;

    public AbstractDatabaseTextanException() {
    }

    public AbstractDatabaseTextanException(String message) {
        super(message);
    }

    public AbstractDatabaseTextanException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractDatabaseTextanException(Throwable cause) {
        super(cause);
    }

    public AbstractDatabaseTextanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AbstractDatabaseTextanException(Object tag) {
        this.tag = tag;
    }

    public AbstractDatabaseTextanException(Object tag, String message) {
        super(message);
        this.tag = tag;
    }

    public AbstractDatabaseTextanException(Object tag, String message, Throwable cause) {
        super(message, cause);
        this.tag = tag;
    }

    public AbstractDatabaseTextanException(Object tag, Throwable cause) {
        super(cause);
        this.tag = tag;
    }

    public AbstractDatabaseTextanException(Object tag, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.tag = tag;
    }

    
    public Object getTag() {
        return tag;
    }

    protected void setTag(Object tag) {
        this.tag = tag;
    }
    
    
}
