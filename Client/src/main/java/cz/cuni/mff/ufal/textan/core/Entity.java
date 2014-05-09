package cz.cuni.mff.ufal.textan.core;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.ObjectOccurrence;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Occurrence;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Client side representation of
 * {@link cz.cuni.mff.ufal.textan.commons.models.Entity}.
 */
public class Entity {

    /** {@link #candidates}'s comparator. */
    final static public Comparator<Pair<Double, Object>> COMPARATOR =
            (p1, p2) -> Double.compare(p1.getFirst(), p2.getFirst());

    /** String value. */
    private final String value;

    /** Position in text. */
    private final int position;

    /** Length. */
    private final int length;

    /** Object Type. */
    private final ObjectType type;

    /** Object candidates for this entity. */
    private final List<Pair<Double, Object>> candidates = new ArrayList<>();

    /** Selected candidate. */
    private Object candidate = null;

    /**
     * Constructor for creating from blue print.
     * @param entity entity blue print
     */
    public Entity(final cz.cuni.mff.ufal.textan.commons.models.Entity entity) {
        value = entity.getValue();
        position = entity.getPosition();
        length = entity.getLength();
        type = new ObjectType(entity.getType());
    }

    /**
     * Contructor for creating from independent values.
     * @param value alias
     * @param position position
     * @param length length
     * @param type type
     */
    public Entity(final String value, final int position, final int length, final ObjectType type) {
        this.value = value;
        this.position = position;
        this.length = length;
        this.type = type;
    }

    /**
     * Returns selected candidate.
     * @return selected candidate
     */
    public Object getCandidate() {
        return candidate;
    }

    /**
     * Sets selected candidate.
     * @param candidate new selected candidate
     */
    public void setCandidate(Object candidate) {
        this.candidate = candidate;
    }

    /**
     * Returns object candidates.
     * @return object candidates
     */
    public List<Pair<Double, Object>> getCandidates() {
        return candidates;
    }

    /**
     * Returns length.
     * @return length
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns position.
     * @return position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns type.
     * @return type
     */
    public ObjectType getType() {
        return type;
    }

    /**
     * Returns value.
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates new commons Entity.
     * @return new commons Entity
     */
    public cz.cuni.mff.ufal.textan.commons.models.Entity toEntity() {
        final cz.cuni.mff.ufal.textan.commons.models.Entity result =
                new cz.cuni.mff.ufal.textan.commons.models.Entity();
        result.setValue(value);
        result.setPosition(position);
        result.setLength(length);
        result.setType(type.toObjectType());
        return result;
    }

    /**
     * Creates new webservice ObjectOccurrence.
     * @return new webservice ObjectOccurrence
     */
    public ObjectOccurrence toObjectOccurrence() {
        final ObjectOccurrence occurrence = new ObjectOccurrence();
        occurrence.setObjectId(candidate.getId());
        final Occurrence o = new Occurrence();
        o.setPosition(position);
        o.setValue(value);
        occurrence.setAlias(o);
        return occurrence;
    }
}
