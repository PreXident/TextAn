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

    public Assignment(Entity entity, List<Pair<Object, Float>> ratedObjects) {
        this.entity = entity;
        this.ratedObjects = ratedObjects;
    }

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
}
