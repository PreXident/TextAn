package cz.cuni.mff.ufal.textan.core.processreport;

import cz.cuni.mff.ufal.textan.commons.models.documentprocessor.GetProblemsResponse;
import cz.cuni.mff.ufal.textan.core.*;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple wrapper of {@link GetProblemsResponse}.
 */
public class Problems {

    /** New Objects. */
    private final List<Object> newObjects;

    /** New Relations. */
    private final List<Relation> newRelations;

    /** Newly joined Objects. */
    private final List<JoinedObject> joinedObjects;

    /** Flag indicating whether the document has been changed. */
    private final boolean changed;

    /** Flag indicating whether the document has already been processed. */
    private final boolean processed;

    /**
     * Testing constructor.
     */
    //TODO remove testing constructor
    public Problems() {
        this.newObjects = Arrays.asList(new Object(-1, new ObjectType(-1, "XXX"), Arrays.asList("xxx", "zzz")));
        this.newRelations = Arrays.asList(new Relation(-1, new RelationType(-1, "qqq")));
        this.joinedObjects = Arrays.asList(new JoinedObject());
        changed = true;
        processed = true;
    }

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
        changed = response.isDocumentChanged();
        processed = response.isDocumentProcessed();
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

    /**
     * Returns whether the document has been changed.
     * @return true if the document has been changed, false otherwise
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Returns whether the document has already been processed.
     * @return true if the document has been processed, false otherwise
     */
    public boolean isProcessed() {
        return processed;
    }
}
