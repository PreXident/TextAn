package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Provides information about graphs.
 */
public interface IGrapher {

    /**
     * Returns predicate to test if the object is the center of the graph.
     * @return predicate deciding the center of the graph
     */
    Predicate<Object> getCenterer();

    /**
     * Returns distance for fetching related objects.
     * @return distance for fetching related objects
     */
    int getDistance();

    /**
     * Sets distance for fetching related objects.
     * @param distance new distance
     */
    void setDistance(int distance);

    /**
     * Returns graph for given root and distance
     * @return graph for given root and distance
     * @throws IdNotFoundException if root id is not valid
     */
    public Graph getGraph() throws IdNotFoundException;

    /**
     * Returns whether the grapher is ready to provide graph.
     * If false is returned, additional information (eg. rootId, distance...)
     * must be provided to properly fetch the graph from server.
     * @return true if the graph can be provided, false otherwise
     * @see #getGraph()
     */
    boolean isReady();

    /**
     * Returns root id.
     * @return root id
     */
    long getRootId();

    /**
     * Sets new root id.
     * @param id new root id
     */
    void setRootId(long id);

    /**
     * Sets object types to ignore.
     * @param objectTypes object types to ignore
     */
    void setIgnoredObjectTypes(Collection<ObjectType> objectTypes);

    /**
     * Sets relation types to ignore.
     * @param relationTypes relation types to ignore
     */
    void setIgnoredRelationTypes(Collection<RelationType> relationTypes);

    /**
     * Returns title of the graph.
     * @return title of the graph
     */
    String getTitle();
}
