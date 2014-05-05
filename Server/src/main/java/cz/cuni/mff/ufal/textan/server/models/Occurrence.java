package cz.cuni.mff.ufal.textan.server.models;

/**
 * @author Petr Fanta
 */
public class Occurrence {

    private final String value;
    private final int position;

    public Occurrence(String value, int position) {
        this.position = position;
        this.value = value;
    }

    public static Occurrence fromCommonsOccurrence(cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Occurrence commonsOccurrence) {
        return new Occurrence(commonsOccurrence.getValue(), commonsOccurrence.getPosition());
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
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
