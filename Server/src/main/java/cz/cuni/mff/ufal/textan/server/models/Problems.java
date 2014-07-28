package cz.cuni.mff.ufal.textan.server.models;

import java.util.List;

/**
 * @author Petr Fanta
 */
public class Problems {

    private final List<Object> newObjects;
    private final List<Relation> newRelations;
    private final List<JoinedObject> newJoinedObjects;

    public Problems(List<Object> newObjects, List<Relation> newRelations, List<JoinedObject> newJoinedObjects) {
        this.newObjects = newObjects;
        this.newRelations = newRelations;
        this.newJoinedObjects = newJoinedObjects;
    }

    public List<Object> getNewObjects() {
        return newObjects;
    }

    public List<Relation> getNewRelations() {
        return newRelations;
    }

    public List<JoinedObject> getNewJoinedObjects() {
        return newJoinedObjects;
    }
}
