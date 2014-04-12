package cz.cuni.mff.ufal.textan.server.services;

/**
 * The exception that is thrown when object with specific identifier not found in the system.
 * @author Petr Fanta
 */
public class IdNotFoundException extends Exception {

    private final String fieldName;
    private final long fieldValue;

    /**
     * Instantiates a new Id not found exception.
     *
     * @param fieldName the name of the identifier
     * @param fieldValue the value of the identifier
     */
    public IdNotFoundException(String fieldName, long fieldValue) {
        super(
                new StringBuilder()
                        .append("The identifier '").append(fieldName)
                        .append("' with value ").append(fieldValue).append(" not found.")
                        .toString()
        );

        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Instantiates a new Id not found exception.
     *
     * @param message the message
     * @param fieldName the name of the identifier
     * @param fieldValue the value of the identifier
     */
    public IdNotFoundException(String message, String fieldName, long fieldValue) {
        super(message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Gets name of identifier.
     *
     * @return the identifier name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets value of identifier.
     *
     * @return the identifier value
     */
    public long getFieldValue() {
        return fieldValue;
    }
}


