/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.exceptions;

/**
 *
 * @author Vaclav Pernicka
 */
public abstract class AbstractDatabaseTextanRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 30001;
    Object tag = null;

    public AbstractDatabaseTextanRuntimeException() {
    }

    public AbstractDatabaseTextanRuntimeException(String message) {
        super(message);
    }

    public AbstractDatabaseTextanRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractDatabaseTextanRuntimeException(Throwable cause) {
        super(cause);
    }

    public AbstractDatabaseTextanRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AbstractDatabaseTextanRuntimeException(Object tag) {
        this.tag = tag;
    }

    public AbstractDatabaseTextanRuntimeException(Object tag, String message) {
        super(message);
        this.tag = tag;
    }

    public AbstractDatabaseTextanRuntimeException(Object tag, String message, Throwable cause) {
        super(message, cause);
        this.tag = tag;
    }

    public AbstractDatabaseTextanRuntimeException(Object tag, Throwable cause) {
        super(cause);
        this.tag = tag;
    }

    public AbstractDatabaseTextanRuntimeException(Object tag, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
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
