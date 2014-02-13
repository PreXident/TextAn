package cz.cuni.mff.ufal.textan.commons.models.adapters;

import cz.cuni.mff.ufal.textan.commons.models.Object;
import cz.cuni.mff.ufal.textan.commons.models.Relation;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**
 * Adapting {@link Graph}.
 */
public class AdaptedGraph {
    protected List<Object> nodes;
    protected List<Relation> edges;

    @XmlElement
    public List<Object> getNodes() {
        return nodes;
    }

    public void setNodes(List<Object> nodes) {
        this.nodes = nodes;
    }

    @XmlElement
    public List<Relation> getEdges() {
        return edges;
    }

    public void setEdges(List<Relation> edges) {
        this.edges = edges;
    }
}
