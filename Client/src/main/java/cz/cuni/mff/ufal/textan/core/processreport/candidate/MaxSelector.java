package cz.cuni.mff.ufal.textan.core.processreport.candidate;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
import java.util.List;
import java.util.Optional;

/**
 * Selects candidate with maximal score.
 */
public class MaxSelector implements ICandidateSelector {

    @Override
    public String getId() {
        return "MaxSelector";
    }

    @Override
    public String getDescKey() {
        return "candidate.max";
    }

    @Override
    public String getDesc() {
        return "Maximal score";
    }

    @Override
    public Object selectCandidate(List<Pair<Double, Object>> candidates) {
        final Optional<Pair<Double, Object>> max =
                candidates.stream().max(Entity.COMPARATOR);
        return max.isPresent() ? max.get().getSecond() : null;
    }
}
