package cz.cuni.mff.ufal.textan.commons.models;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Petr Fanta on 24.1.14.
 */
public class Graph {

    private TreeMap<Integer, Object> nodes;
    private ArrayList<Relation> edges;

    public Graph(TreeMap<Integer, Object> nodes, ArrayList<Relation> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    //todo: add implementation
}
