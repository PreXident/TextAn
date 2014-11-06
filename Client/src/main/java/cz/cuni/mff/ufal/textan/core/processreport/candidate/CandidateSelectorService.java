package cz.cuni.mff.ufal.textan.core.processreport.candidate;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Service providing ICandidateSelectors.
 */
public class CandidateSelectorService {

    /** Holds the singleton's intance. */
    static private volatile CandidateSelectorService instance = null;

    /**
     * Returns singleton's instance.
     * @return singleton's instance
     */
    static public CandidateSelectorService getInstance() {
        if (instance == null) { //double checking
            synchronized (CandidateSelectorService.class) {
                if (instance == null) {
                    instance = new CandidateSelectorService();
                }
            }
        }
        return instance;
    }

    private final ServiceLoader<ICandidateSelector> loader =
            ServiceLoader.load(ICandidateSelector.class);

    /**
     * Only constructor.
     */
    private CandidateSelectorService() { }

    /**
     * Returns new list of candidate selectors.
     * @return new list of candidate selectors
     */
    public List<ICandidateSelector> listSelectors() {
        final List<ICandidateSelector> result = new ArrayList<>();
        for (ICandidateSelector candidate: loader) {
            result.add(candidate);
        }
        return result;
    }

    /**
     * Returns selector with the given id.
     * @param id selector id
     * @return selector with the given id
     * @throws IllegalArgumentException if no selector with given if is found
     */
    public ICandidateSelector getSelector(final String id) {
        for (ICandidateSelector selector: loader) {
            if (selector.getId().equals(id)) {
                return selector;
            }
        }
        throw new IllegalArgumentException(
                String.format("No candidate selector with id \"%s\" found", id));
    }
}
