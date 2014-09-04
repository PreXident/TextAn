package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsResponse;
import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.*;
import cz.cuni.mff.ufal.textan.core.Object;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple wrapper of {@link GetProblemsResponse}.
 */
public class Problems implements Serializable {

    /** New Objects. */
    private final List<Object> newObjects;

    /** New Relations. */
    private final List<Relation> newRelations;

    /** Newly joined Objects. */
    private final List<JoinedObject> joinedObjects;

    /**
     * Constructs Problems from GetProblemsResponse.
     * @param response blue print
     */
    public Problems(final GetProblemsResponse response) {
        final Map<Long, Object> objects = response.getRelationObjects().stream()
                .collect(Collectors.toMap(
                        cz.cuni.mff.ufal.textan.commons.models.Object::getId,
                        Object::new));
        newObjects = response.getNewObjects().stream()
                .map(Object::new)
                .collect(Collectors.toList());
        newRelations = response.getNewRelations().stream()
                .map(rel -> new Relation(rel, objects))
                .collect(Collectors.toList());
        joinedObjects = response.getNewJoinedObjects().stream()
                .map(JoinedObject::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns new Objects.
     * @return new Objects
     */
    public List<Object> getNewObjects() {
        return newObjects;
    }

    /**
     * Returns new Relations.
     * @return new Relations
     */
    public List<Relation> getNewRelations() {
        return newRelations;
    }

    /**
     * Returns newly joined Objects.
     * @return newly joined Objects
     */
    public List<JoinedObject> getJoinedObjects() {
        return joinedObjects;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("New objects:\n");
        for (Object obj : newObjects) {
            builder.append("* ");
            builder.append(obj.toString());
            builder.append('\n');
        }
        builder.append("New relations:\n");
        for (Relation relation : newRelations) {
            builder.append("* ");
            builder.append(relation.toString());
            builder.append('\n');
            for (Triple<Integer, String, Object> triple : relation.getObjects()) {
                builder.append("\t+ ");
                builder.append(triple.getFirst());
                builder.append(" - ");
                builder.append(triple.getSecond());
                builder.append(" - ");
                builder.append(triple.getThird());
                builder.append('\n');
            }
        }
        builder.append("Joined objects:\n");
        for (JoinedObject joined : joinedObjects) {
            builder.append("* ");
            builder.append(joined.root);
            builder.append('\n');
            for (Object object : joined.children) {
                builder.append("\t+ ");
                builder.append(object);
                builder.append('\n');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
