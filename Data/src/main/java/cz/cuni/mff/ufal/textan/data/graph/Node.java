/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.graph;

import java.util.Objects;

/**
 *
 * @author Václav Pernička
 */
public class Node {
    long id;
    String name;

    Node(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
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

    /**
     *
     * @return
     */
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

    void setName(String name) {
        this.name = name;
    }
    
}
