package cz.cuni.mff.ufal.textan.gui.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Service providing IHyperGraphProviders.
 */
public final class HyperGraphService {

    /** Holds the singleton's intance. */
    static private volatile HyperGraphService instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static HyperGraphService getInstance() {
        if (instance == null) { //double checking
            synchronized (HyperGraphService.class) {
                if (instance == null) {
                    instance = new HyperGraphService();
                }
            }
        }
        return instance;
    }

    private final ServiceLoader<IHyperGraphProvider> loader =
            ServiceLoader.load(IHyperGraphProvider.class);

    /**
     * Only constructor.
     */
    private HyperGraphService() { }

    /**
     * Returns new list of hypergraph providers.
     * @return new list of hypergraph providers
     */
    public List<IHyperGraphProvider> listProviders() {
        final List<IHyperGraphProvider> result = new ArrayList<>();
        for (IHyperGraphProvider provider: loader) {
            result.add(provider);
        }
        return result;
    }

    /**
     * Returns provider with the given id.
     * @param id provider id
     * @return provider with the given id
     * @throws IllegalArgumentException if no provider with given if is found
     */
    public IHyperGraphProvider getProvider(final String id) {
        for (IHyperGraphProvider provider: loader) {
            if (provider.getId().equals(id)) {
                return provider;
            }
        }
        throw new IllegalArgumentException(
                String.format("No hypergraph provider with id \"%s\" found", id));
    }
}
