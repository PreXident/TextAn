package cz.cuni.mff.ufal.textan.server.models;

/**
 * Represents a subset of a text.
 *
 * @author Petr Fanta
 */
public class Occurrence {

    private final String value;
    private final int position;

    /**
     * Instantiates a new Occurrence.
     *
     * @param value the value
     * @param position the position
     */
    public Occurrence(String value, int position) {
        this.position = position;
        this.value = value;
    }

    /**
     * Concerts a {@link cz.cuni.mff.ufal.textan.commons.models.Occurrence} to a {@link cz.cuni.mff.ufal.textan.server.models.Occurrence}
     *
     * @param commonsOccurrence the commons occurrence
     * @return the occurrence
     */
    public static Occurrence fromCommonsOccurrence(cz.cuni.mff.ufal.textan.commons.models.Occurrence commonsOccurrence) {
        if (commonsOccurrence != null) {
            return new Occurrence(commonsOccurrence.getValue(), commonsOccurrence.getPosition());
        } else {
            return null;
        }
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    public cz.cuni.mff.ufal.textan.commons.models.Occurrence toCommonsOccurrence() {
        cz.cuni.mff.ufal.textan.commons.models.Occurrence commonsOccurrence = new cz.cuni.mff.ufal.textan.commons.models.Occurrence();
        commonsOccurrence.setPosition(position);
        commonsOccurrence.setValue(value);

        return commonsOccurrence;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Occurrence that = (Occurrence) o;

        if (position != that.position) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + position;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Occurrence{");
        sb.append("value='").append(value).append('\'');
        sb.append(", position=").append(position);
        sb.append('}');
        return sb.toString();
    }
}
