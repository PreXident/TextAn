package cz.cuni.mff.ufal.textan.assigner;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.assigner.data.Entity;

import java.util.List;
import java.util.Map;

/**
 * Interface for bean automatical assigning potential objects from the database
 * to named entities.
 */
public interface IObjectAssigner {

    /**
     * Assigns list of possible candidates to each entity and their ranking.
     * For each given entity returns a list containing pairs consisting of
     * candidate object and its score. Combines results from both
     * {@link #heuristicRanking(java.lang.String, java.util.List, int)
     * heuristicRanking} and
     * {@link #machineLearningRanking(java.lang.String, java.util.List, int)
     * machineLearningRanking}. 
     * @param document document containing the entities
     * @param entities list of recognized named entities
     * @param topK maximal number of candidates for each entity
     * @return list of ranked candidates for each entity
     */
    Map<Entity, List<Pair<Long, Double>>> createObjectRanking(String document, List<Entity> entities, int topK);

    /**
     * Part of creating object ranking.
     * Uses heuristic approach to create list of ranked candidates for each
     * given entity.
     * @param document document containing the entities
     * @param entities list of recognized named entities
     * @param topK maximal number of candidates for each entity
     * @return list of ranked candidates for each entity
     * @see #createObjectRanking(java.lang.String, java.util.List, int)
     */
    Map<Entity, List<Pair<Long, Double>>> heuristicRanking(String document, List<Entity> entities, int topK);

    /**
     * Runs Weka machine learning to learn the model used for
     * {@link #machineLearningRanking(java.lang.String, java.util.List, int)
     * machineLearningRanking} from the database.
     */
    void learn();

    /**
     * Part of creating object ranking.
     * Uses Weka machine learning.
     * @param document document containing the entities
     * @param entities list of recognized named entities
     * @param topK maximal number of candidates for each entity
     * @return list of ranked candidates for each entity
     * @see #createObjectRanking(java.lang.String, java.util.List, int)
     */
    Map<Entity, List<Pair<Long, Double>>> machineLearningRanking(String document, List<Entity> entities, int topK);
}