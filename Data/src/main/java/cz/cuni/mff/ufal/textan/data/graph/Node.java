package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Objects;

/**
 * Represents node in a graph.
 * It is abstract superclass of ObjectNode and RelationNode.
 *
 * @see Graph
 * @author Vaclav Pernicka
 */
public abstract class Node {
    
    public static final long UNKNOWN_NODE_ID = -1;

    long id;
    String name;

    public Node(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", name=" + name + '}';
    }

    /**
     *
     * @return id of the corresponding Object or Relation
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id new id
     */
    void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return name of the object or type of relation
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name new name
     */
    void setName(String name) {
        this.name = name;
    }
}
