package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.commons.utils.Triple;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.ObjectType;
import cz.cuni.mff.ufal.textan.core.Relation;
import cz.cuni.mff.ufal.textan.core.RelationType;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Common ancestor for graphers.
 */
public abstract class AbstractGrapher implements IGrapher {

    /** Distance from root to fetch. */
    protected int distance = -1;

    /** Root central object id. */
    protected long rootId = -1;

    /** Object types to ignore. */
    final protected Set<ObjectType> ignoredObjectTypes = new HashSet<>();

    /** Relation types to ignore. */
    final protected Set<RelationType> ignoredRelationTypes = new HashSet<>();

    /** Client connecting to webservices. */
    final protected Client client;

    /** Flag indicating whether the graph is fetched and matches current parameters. */
    protected boolean fetched = false;

    /**
     * Flag indicating whether the graph is filtered and matches current parameters.
     * Value is valid only if the graph is fetched.
     */
    protected boolean filtered = false;

    /** Wrapped original graph. */
    protected Graph graph;

    /** Wrapped filtered graph. */
    protected Graph filteredGraph;

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public AbstractGrapher(Client client) {
        this.client = client;
    }

    @Override
    public final Graph getGraph() throws IdNotFoundException {
        if (!fetched) {
            graph = fetchGraph();
            fetched = true;
            filteredGraph = filterGraph();
            filtered = true;
        } else if (!filtered) {
            filteredGraph = filterGraph();
        }
        return filteredGraph;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public void setDistance(int distance) {
        if (this.distance != distance) {
            fetched = false;
            this.distance = distance;
        }
    }

    @Override
    public boolean isReady() {
        return rootId > 0 && distance >= 0;
    }

    /**
     * Synchronizes oldIgnored and newIgnored collections.
     * @param <T> content of collections
     * @param oldIgnored old ignored types
     * @param newIgnored new ignored types
     */
    protected <T> void setIgnoredTypes(final Set<T> oldIgnored,
            final Collection<T> newIgnored) {
        final boolean noChange =
                oldIgnored.size() == newIgnored.size()
                && oldIgnored.containsAll(newIgnored);
        if (noChange) {
            return;
        }
        filtered = false;
        oldIgnored.clear();
        oldIgnored.addAll(newIgnored);
    }

    @Override
    public void setIgnoredObjectTypes(final Collection<ObjectType> objectTypes) {
        setIgnoredTypes(ignoredObjectTypes, objectTypes);
    }

    @Override
    public void setIgnoredRelationTypes(final Collection<RelationType> relationTypes) {
        setIgnoredTypes(ignoredRelationTypes, relationTypes);
    }

    @Override
    public long getRootId() {
        return rootId;
    }

    @Override
    public void setRootId(long rootId) {
        if (this.rootId != rootId) {
            fetched = false;
            this.rootId = rootId;
        }
    }

    @Override
    public void clearCache() {
        fetched = false;
        graph = null;
        filteredGraph = null;
    }

    /**
     * Calls relevant methods of client to get the graph.
     * @return graph to be wrapped by this grapher
     * @throws IdNotFoundException if id error occurs
     */
    protected abstract Graph fetchGraph() throws IdNotFoundException;

    /**
     * Filters the content of the graph by ignored types.
     * @return filtered graph
     */
    protected abstract Graph filterGraph();

    /**
     * Returns filtered graph originating in root.
     * @param root graph root object
     * @return filtered graph originating in root
     */
    protected Graph filterGraph(final Object root) {
        return filterGraph(Arrays.asList(root));
    }

    /**
     * Returns filtered graph originating in roots.
     * @param roots graph roots object
     * @return filtered graph originating in roots
     */
    protected Graph filterGraph(final Collection<Object> roots) {
        final Set<Relation> edges = graph.getEdges();
        // crate object->relations mapping
        final Map<cz.cuni.mff.ufal.textan.core.Object, Set<Relation>> objRels = new HashMap<>();
        for (Relation relation : edges) {
            for (Triple<Integer, String, cz.cuni.mff.ufal.textan.core.Object> triple : relation.getObjects()) {
                final cz.cuni.mff.ufal.textan.core.Object obj = triple.getThird();
                if (!ignoredObjectTypes.contains(obj.getType())) {
                    Set<Relation> rels =  objRels.get(obj);
                    if (rels == null) {
                        rels = new HashSet<>();
                        objRels.put(obj, rels);
                    }
                    rels.add(new Relation(relation));
                }
            }
        }
        //initilization
        final Map<Long, cz.cuni.mff.ufal.textan.core.Object> newNodes = new HashMap<>();
        final Set<Relation> newEdges = new HashSet<>();
        final Set<Relation> doneRelations = new HashSet<>(); //processed relations
        final Set<cz.cuni.mff.ufal.textan.core.Object> doneObjects = new HashSet<>(); //processed objects
        final Deque<cz.cuni.mff.ufal.textan.core.Object> stack = new ArrayDeque<>(); //objects whose relations need processing
        for (Object root : roots) {
            newNodes.put(root.getId(), root);
            doneObjects.add(root);
            stack.add(root);
        }
        //filtering
        while (!stack.isEmpty()) {
            final cz.cuni.mff.ufal.textan.core.Object obj = stack.pop();
            final Set<Relation> rels = objRels.get(obj);
            if (rels == null) {
                continue;
            }
            for (Relation rel : rels) {
                if (doneRelations.contains(rel)) {
                    continue;
                }
                doneRelations.add(rel);
                if (ignoredRelationTypes.contains(rel.getType())) {
                    continue;
                }
                final Set<Triple<Integer, String, cz.cuni.mff.ufal.textan.core.Object>> newObjs = new HashSet<>();
                for (Triple<Integer, String, cz.cuni.mff.ufal.textan.core.Object> triple : rel.getObjects()) {
                    if (!ignoredObjectTypes.contains(triple.getThird().getType())) {
                        newObjs.add(triple);
                        if (!doneObjects.contains(triple.getThird())) {
                            doneObjects.add(triple.getThird());
                            newNodes.put(triple.getThird().getId(), triple.getThird());
                            stack.add(triple.getThird());
                        }
                    }
                }
                //add only if there are more objects connected
                //or if the original relation was unary too
                if (newObjs.size() > 1 || rel.getObjects().size() == 1) {
                    newEdges.add(rel);
                    rel.getObjects().clear();
                    rel.getObjects().addAll(newObjs);
                }
            }
        }
        //use new nodes and edges
        return new Graph(newNodes, newEdges);
    }
}
