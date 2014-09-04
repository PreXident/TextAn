package cz.cuni.mff.ufal.textan.server.models;

import java.util.List;

/**
 * A service layer representation of problems that occurs during saving.
 * @author Petr Fanta
 */
public class Problems {

    private final List<Object> newObjects;
    private final List<Relation> newRelations;
    private final List<JoinedObject> newJoinedObjects;

    /**
     * Instantiates a new Problems
     * @param newObjects the new objects in the database
     * @param newRelations the new relations in the database
     * @param newJoinedObjects the new joined object in the database.
     */
    public Problems(List<Object> newObjects, List<Relation> newRelations, List<JoinedObject> newJoinedObjects) {
        this.newObjects = newObjects;
        this.newRelations = newRelations;
        this.newJoinedObjects = newJoinedObjects;
    }

    /**
     * Gets the new objects in the database.
     * @return list of objects
     */
    public List<Object> getNewObjects() {
        return newObjects;
    }

    /**
     * Gets the new relations in the database.
     * @return list of relations
     */
    public List<Relation> getNewRelations() {
        return newRelations;
    }

    /**
     * Gets the new joined object in the database
     * @return list of joined objects
     */
    public List<JoinedObject> getNewJoinedObjects() {
        return newJoinedObjects;
    }
}
