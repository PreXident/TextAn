package cz.cuni.mff.ufal.textan.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Client side representation of {@link cz.cuni.mff.ufal.textan.commons.models.Entity}.
 */
public class Entity {

    /** String value. */
    private final String value;

    /** Position in text. */
    private final int position;

    /** Length. */
    private final int length;

    /** Object Type. */
    private final int type;

    /** Object candidates for this entity. */
    private final Map<Double, Object> candidates = new HashMap<>();

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
        type = (int) entity.getType();
    }

    /**
     * Contructor for creating from independent values.
     * @param value alias
     * @param position position
     * @param length length
     * @param type type
     */
    public Entity(String value, int position, int length, int type) {
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
    public Map<Double, Object> getCandidates() {
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
    public int getType() {
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
        result.setType(type);
        return result;
    }
}
