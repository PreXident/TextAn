package cz.cuni.mff.ufal.textan.server.models;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;

import java.util.List;

/**
 * A service layer representation of a assignment of an entity to objects with score.
 * @author Petr Fanta
 */
public class Assignment {

    private final Entity entity;
    private final List<Pair<Object, Float>> ratedObjects;

    /**
     * Instantiates a new Assignment.
     *
     * @param entity the entity
     * @param ratedObjects the list of objects with their score
     */
    public Assignment(Entity entity, List<Pair<Object, Float>> ratedObjects) {
        this.entity = entity;
        this.ratedObjects = ratedObjects;
    }

    /**
     * Converts {@link cz.cuni.mff.ufal.textan.server.models.Assignment} to {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment}.
     *
     * @return the {@link cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment}
     */
    public cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment toCommonsAssignment() {

        cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment commonsAssignment = new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment();
        commonsAssignment.setEntity(entity.toCommonsEntity());

        for (Pair<Object, Float> ratedObject : ratedObjects) {
            cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment.RatedObject commonsRatedObject = new cz.cuni.mff.ufal.textan.commons.models.documentprocessor.Assignment.RatedObject();
            commonsRatedObject.setObject(ratedObject.getFirst().toCommonsObject());
            commonsRatedObject.setScore(ratedObject.getSecond());
            commonsAssignment.getRatedObjects().add(commonsRatedObject);
        }

        return commonsAssignment;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assignment that = (Assignment) o;

        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (ratedObjects != null ? !ratedObjects.equals(that.ratedObjects) : that.ratedObjects != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (ratedObjects != null ? ratedObjects.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Assignment{");
        sb.append("entity=").append(entity);
        sb.append(", ratedObjects=").append(ratedObjects);
        sb.append('}');
        return sb.toString();
    }
}
