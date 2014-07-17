package cz.cuni.mff.ufal.textan.core.graph;

import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Graph;
import cz.cuni.mff.ufal.textan.core.IdNotFoundException;
import cz.cuni.mff.ufal.textan.core.Relation;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Provides information about graphs centered around relations.
 */
public class RelationGrapher extends AbstractGrapher {

    /**
     * Only constructor.
     * @param client client for connecting to webservices
     */
    public RelationGrapher(Client client) {
        super(client);
    }

    @Override
    public Predicate<Object> getCenterer() {
        return object -> {
            if (object instanceof Relation) {
                return ((Relation) object).getId() == rootId;
            } else {
                return false;
            }
        };
    }

    @Override
    public String getTitle() {
        final Optional<Relation> center = graph.getEdges().stream()
                .filter(rel -> rel.getId() == rootId)
                .findFirst();
        if (center.isPresent()) {
            final Relation rel = center.get();
            return rel.toString() + ": " + rel.getAnchorString();
        } else {
            return String.valueOf(rootId);
        }
    }

    @Override
    protected Graph fetchGraph() throws IdNotFoundException {
        return client.getRelationGraph(rootId, distance);
    }
}
