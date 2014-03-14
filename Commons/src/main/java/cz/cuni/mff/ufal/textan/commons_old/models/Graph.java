package cz.cuni.mff.ufal.textan.commons_old.models;

import cz.cuni.mff.ufal.textan.commons_old.models.adapters.GraphAdapter;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by Petr Fanta on 24.1.14.
 */
@XmlJavaTypeAdapter(GraphAdapter.class)
public class Graph {

    protected List<Object> nodes;
    protected List<Relation> edges;

    public Graph(List<Object> nodes, List<Relation> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    //todo: add implementation

    public List<Object> getNodes() {
        return nodes;
    }

    public List<Relation> getEdges() {
        return edges;
    }
}
