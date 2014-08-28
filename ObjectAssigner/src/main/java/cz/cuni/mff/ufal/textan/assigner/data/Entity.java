package cz.cuni.mff.ufal.textan.assigner.data;

/**
 * Simple holder of properties related to entities.
 */
public class Entity{

    /** Entity text. */
    final private String text;

    /** Entity type id. */
    final long type;

    /**
     * Only constructor.
     * @param text entity text
     * @param typeID entity type id
     */
    public Entity(String text, long typeID) {
        this.text = text;
        this.type = typeID;
    }

    /**
     * Returns entity text.
     * @return entity text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns entity type id.
     * @return entity type id
     */
    public long getType() {
        return type;
    }

    //TODO check if this makes sense
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Entity entity = (Entity) o;

        if (type != entity.type) return false;
        if (!text.equals(entity.text)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + (int) (type ^ (type >>> 32));
        return result;
    }
}
