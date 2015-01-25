package cz.cuni.mff.ufal.textan.core.processreport.candidate;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.List;
import java.util.Optional;

/**
 * Selects candidate with maximal score, but it must have score better than the
 * second one by at least 10%.
 */
public class RelativeSelector implements ICandidateSelector {

    /** Second best candidate must be under this threshold to select the best candidate. */
    static private final double THRESHOLD = 0.9;

    @Override
    public String getId() {
        return "RelativeSelector";
    }

    @Override
    public String getDescKey() {
        return "candidate.relative";
    }

    @Override
    public String getDesc() {
        return "Relative score";
    }

    @Override
    public Object selectCandidate(List<Pair<Double, Object>> candidates) {
        final Optional<Pair<Double, Object>> max =
                candidates.stream().max(Entity.COMPARATOR);
        if (!max.isPresent()) {
            return null;
        } else {
            final Object best = max.get().getSecond();
            final Optional<Pair<Double, Object>> second = candidates.stream()
                    .filter(p -> !p.getSecond().equals(best))
                    .max(Entity.COMPARATOR);
            if (!second.isPresent()) {
                return best;
            } else {
                if (max.get().getFirst() * THRESHOLD > second.get().getFirst()) {
                    return best;
                } else {
                    return null;
                }
            }
        }
    }
}
