package cz.cuni.mff.ufal.textan.core.processreport.candidate;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.List;

/**
 * Provides method to select candidate from candidate list.
 */
public interface ICandidateSelector {

    /**
     * Returns unique identifier for the selector.
     * @return unique identifier for the selector
     */
    String getId();

    /**
     * Returns localization key for the description.
     * @return localization key for the description
     */
    String getDescKey();

    /**
     * Returns English description if no localization is found for descKey.
     * @return English description if no localization is found for descKey
     */
    String getDesc();

    /**
     * Returns selected candidate from the list of candidates.
     * @param candidates list of possible candidates
     * @return selected candidate
     */
    Object selectCandidate(List<Pair<Double, Object>> candidates);
}
