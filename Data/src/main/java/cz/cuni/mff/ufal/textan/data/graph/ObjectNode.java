package cz.cuni.mff.ufal.textan.data.graph;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;

/**
 * Represents a node that was created from an object.
 *
 * @author Vaclav Pernicka
 * @see Node
 * @see Graph
 */
public class ObjectNode extends Node {
    public static final String UNKNOWN_OBJECT_NAME = "Unknown Object";

    public ObjectNode(final ObjectTable obj) {
        this(obj == null ? Node.UNKNOWN_NODE_ID : obj.getId(),
                obj == null ? UNKNOWN_OBJECT_NAME : obj.getData());
    }

    /**
     * constructor
     *
     * @param id   id of object
     * @param name name of the object
     */
    public ObjectNode(long id, String name) {
        super(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ObjectNode && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "ObjectNode{" + "id=" + id + ", name=\"" + name + "\"}";
    }


}
