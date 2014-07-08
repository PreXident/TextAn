package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsByIdResponse;
import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsFromStringResponse;
import cz.cuni.mff.ufal.textan.core.JoinedObject;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple wrapper of {@link GetProblemsFromStringResponse} and
 * {@link GetProblemsByIdResponse}.
 */
public class Problems {

    /** New Objects. */
    private final List<Object> newObjects;

    /** New Relations. */
    private final List<Relation> newRelations;

    /** Newly joined Objects. */
    private final List<JoinedObject> joinedObjects;

    /**
     * Testing constructor.
     */
    //TODO remove testing constructor
    public Problems() {
        this.newObjects = Arrays.asList(new Object(-1, new ObjectType(-1, "XXX"), Arrays.asList("xxx", "zzz")));
        this.newRelations = Arrays.asList(new Relation(-1, new RelationType(-1, "qqq")));
        this.joinedObjects = Arrays.asList(new JoinedObject());
    }

    /**
     * Constructs Problems from GetProblemsFromStringResponse.
     * @param response blue print
     */
    public Problems(final GetProblemsFromStringResponse response) {
        //TODO replace emptyMap by reponse.getRelationObjects and remove Arrays.asList
        this(Collections.emptyMap(),
                response.getNewObjects(),
                response.getNewRelations(),
                Arrays.asList(response.getNewJoinedObject()));
    }

    /**
     * Constructs Problems from GetProblemsByIdResponse.
     * @param response blue print
     */
    public Problems(final GetProblemsByIdResponse response) {
        //TODO replace emptyMap by reponse.getRelationObjects and remove Arrays.asList
        this(Collections.emptyMap(),
                response.getNewObjects(),
                response.getNewRelations(),
                Arrays.asList(response.getNewJoinedObject()));
    }

    /**
     * Private constructor underlying the others.
     * @param objects id -> object mapping to resolve relation ids
     * @param newObjects new objects
     * @param newRelations new relations
     * @param joinedObjects newly joined objects
     */
    private Problems(final Map<Long, Object> objects,
            final List<cz.cuni.mff.ufal.textan.commons.models.Object> newObjects,
            final List<cz.cuni.mff.ufal.textan.commons.models.Relation> newRelations,
            final List<cz.cuni.mff.ufal.textan.commons.models.JoinedObject> joinedObjects) {
        //TODO remove tests
        this.newObjects = newObjects.stream()
                .map(Object::new)
                .collect(Collectors.toList());
        this.newRelations = newRelations.stream()
                .map(rel -> new Relation(rel, objects))
                .collect(Collectors.toList());
        this.joinedObjects = joinedObjects.stream()
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
}
