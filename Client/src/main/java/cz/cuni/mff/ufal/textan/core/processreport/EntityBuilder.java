package cz.cuni.mff.ufal.textan.core.processreport;

/**
 * Simple class representing marked Entity.
 * Entities do not track their words, words track their entities.
 * To get marked entities, iterate word list.
 */
public class EntityBuilder extends AbstractBuilder {

    /** Type Id. */
    protected final int id;

    /**
     * Only constructor.
     * @param id entity type id
     */
    public EntityBuilder(final int id) {
        this.id = id;
    }

    /**
     * Returns entity id.
     * @return entity id
     */
    public int getId() {
        return id;
    }

    @Override
    protected EntityBuilder extract(final Word word) {
        return word.getEntity();
    }

    @Override
    protected void register(final Word word) {
        word.setEntity(this);
    }

    @Override
    protected void unregister(final Word word) {
        word.setEntity(null);
    }
}
