package cz.cuni.mff.ufal.textan.assigner;

import cz.cuni.mff.ufal.textan.assigner.data.Entity;
import cz.cuni.mff.ufal.textan.assigner.data.EntityInfo;
import cz.cuni.mff.ufal.textan.assigner.learning.TrainWeka;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 * A simple example of an implementation of the IObjectAssigner interface as a
 * Spring bean.
 *
 * @author Petr Fanta
 * @author Tam Hoang
 *         Implement the ranking scheme heuristicRanking(document, list of entities, number of K)
 *         Implement the machine learning scheme machineLearning
 * @author Adam Huječek refactoring
 * @see IObjectAssigner
 */
@Transactional
public class ObjectAssigner implements IObjectAssigner {

    /** Default logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ObjectAssigner.class);

    /** Provides access to Alias table in database. */
    private final IAliasTableDAO aliasTableDAO;

    /** Provides access to ObjectTable table in database. */
    private final IObjectTableDAO objectTableDAO;

    /** Provides access to DocumentTable table in database. */
    private final IDocumentTableDAO documentTableDAO;

    /** Train Weka. */
    private TrainWeka train;

    /** Training model. */
    private Classifier model;

    /**
     * Instantiates a new ObjectAssigner.
     * Uses a constructor injection for an initialization of data access object
     * ({@link cz.cuni.mff.ufal.textan.assigner.configs.ObjectAssignerConfig#textPro(IObjectTableDAO, IAliasTableDAO, IDocumentTableDAO)}).
     *
     * @param aliasTableDAO the alias table DAO
     * @param objectTableDAO the object table DAO
     * @param documentTableDAO the document table DAO
     */
    public ObjectAssigner(
            final IAliasTableDAO aliasTableDAO,
            final IObjectTableDAO objectTableDAO,
            final IDocumentTableDAO documentTableDAO) {
        this.aliasTableDAO = aliasTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.documentTableDAO = documentTableDAO;
    }

    /**
     * Checks relationship between two objects.
     * Objects are considered related if they occured in the same document.
     * @param o1 first object to check
     * @param o2 second object to check
     * @return true if o1 and o2 shares any document, false otherwise
     */
    protected boolean checkRelation(ObjectTable o1, ObjectTable o2) {
        final List<Pair<DocumentTable, Integer>> docs1 =
                documentTableDAO.findAllDocumentsWithObject(o1);
        final List<Pair<DocumentTable, Integer>> docs2 =
                documentTableDAO.findAllDocumentsWithObject(o2);

        // Create a set of document ids
        final Set<Long> ids = new HashSet<>();
        docs1.stream()
                .map(Pair::getFirst)
                .map(DocumentTable::getId)
                .forEach(ids::add);
        // Check match
        return docs2.stream()
                .map(Pair::getFirst);
                .map(DocumentTable::getId)
                .anyMatch(ids::contains);
    }

    /**
     * Creates entity infos for given entities.
     * @param entities entities to create infos for
     * @return entity infos for given entities
     */
    protected List<EntityInfo> createEntityInfos(List<Entity> entities) {
        final List<EntityInfo> infos = new ArrayList<>();
        for (Entity e : entities) {
            final List<ObjectTable> objects = getCloseObjects(e);
            infos.add(new EntityInfo(e, objects));
        }
        return infos;
    }

    /**
     * Combines both ranking approaches - heuristic and machine learning.
     * If the object occurs in both lists, its score is the sum,
     * otherwise gets the result of machine learning.
     * @param document document containing the entities
     * @param entities list of recognized named entities
     * @param topK maximal number of candidates for each entity
     * @return list of ranked candidates for each entity
     */
    @Override
    public Map<Entity, List<Pair<Long, Double>>> combinedObjectRanking(
            final String document,
            final List<Entity> entities,
            final int topK) {
        final Map<Entity, List<Pair<Long, Double>>> mapHR = heuristicRanking(document, entities, 2 * topK);
        final Map<Entity, List<Pair<Long, Double>>> mapML = machineLearningRanking(document, entities, 2 * topK);

        LOG.debug("Starting ObjectAssigner merge of results.");

        final Map<Entity, List<Pair<Long, Double>>> result = new HashMap<>();
        for (Entity entity : mapML.keySet()) {
            final List<Pair<Long, Double>> listML = mapML.get(entity);
            if (mapHR.containsKey(entity)) {
                final List<Pair<Long, Double>> listHR = mapHR.get(entity);

                // Create two list from the pair list of Machine Learning
                final Map<Long, Double> scores = new HashMap<>();
                for (Pair<Long, Double> p : listML) {
                    scores.put(p.getFirst(), p.getSecond());
                }

                // Update the list of eMapML from the list of HR
                for (Pair<Long, Double> p : listHR) {
                    final Double score = scores.get(p.getFirst());
                    if (score != null) {
                        scores.put(p.getFirst(), score + p.getSecond());
                    }
                }

                // Select topK
                final BoundedPriorityQueue<Entry<Long, Double>> tops =
                        new BoundedPriorityQueue<>(topK, (p1, p2) -> {
                            return p1.getValue().compareTo(p2.getValue());
                        });
                for (Entry<Long, Double> entry : scores.entrySet()) {
                    tops.add(entry);
                }
                final ArrayList<Pair<Long, Double>> finalScores = tops.getQueue().stream()
                        .map(e -> new Pair<>(e.getKey(), e.getValue()))
                        .collect(Collectors.toCollection(ArrayList::new));
                result.put(entity, finalScores);
            } else {
                // There is some exception there if two lists does not contain the same key
                // This should never happen!
                LOG.error("Ranking methods do not use the same entities!");
                result.put(entity, listML);
            }
        }
        LOG.debug("Finishing ObjectAssigner merge of results.");
        return result;
    }

    /**
     * Returns objects considered close to the given entity.
     * Firstly tries entity text and type in
     * {@link IObjectTableDAO#findAllByObjTypeAndAliasFullText(long, String)
     * full text} then in
     * {@link IObjectTableDAO#findAllByObjectTypeAndAliasSubStr(long, java.lang.String)
     * substring}.
     * @param e entity to search close objects for
     * @return close objects
     */
    public List<ObjectTable> getCloseObjects(Entity e){
        final List<ObjectTable> matchFullText =
                objectTableDAO.findAllByObjTypeAndAliasFullText(e.getType(), e.getText());
        if(matchFullText.isEmpty()){
            return this.objectTableDAO.findAllByObjectTypeAndAliasSubStr(e.getType(), e.getText());
        } else {
            return matchFullText;
        }
    }

    @Override
    public Map<Entity, List<Pair<Long, Double>>> heuristicRanking(
            final String document,
            final List<Entity> entities,
            final int topK) {
        LOG.debug("Starting ObjectAssigner heuristic ranking.");

        // Initialize result
        final Map<Entity, List<Pair<Long, Double>>> result = new HashMap<>();

        // Initialize the list of entity infos
        final List<EntityInfo> infos = createEntityInfos(entities);

        // Heuristic ranking
        for (int i = 0; i < infos.size(); ++i) {
            final EntityInfo info = infos.get(i);
            final String eText = info.e.getText();

            // Repetitive update the score
            // we only need to go through the upper triangular part of matrix (without diagonal)
            for (int j = i + 1; j < infos.size(); ++j) {
                final EntityInfo otherInfo = infos.get(j);
                final String eOtherText = otherInfo.e.getText();
                if (eText.equalsIgnoreCase(eOtherText)
                        && info.e.getType() == otherInfo.e.getType()) {
                    continue; // Check if they have the same text, not just the same entity
                }
                for (ObjectTable object : info.objects) {
                    for (ObjectTable otherObject : otherInfo.objects) {
                        if (checkRelation(object, otherObject)) {
                            // Increase the score by 1
                            info.increaseScore(object);
                            otherInfo.increaseScore(otherObject);
                        }
                    }
                }
            }

            // Select topK
            final BoundedPriorityQueue<Entry<Long, Double>> tops =
                    new BoundedPriorityQueue<>(topK, (p1, p2) -> {
                        return p1.getValue().compareTo(p2.getValue());
                    });
            for (Entry<Long, Double> entry : info.score.entrySet()) {
                tops.add(entry);
            }

            // Normalize topK and add to result
            double sum = 0; //remains 0, only if there are no candidates
            for (Entry<Long, Double> entry : tops.getQueue()) {
                sum += entry.getValue();
            }
            final List<Pair<Long, Double>> score = new ArrayList<>(topK);
            for (Entry<Long, Double> entry : tops.getQueue()) {
                final Pair<Long, Double> pair = new Pair<>(
                        entry.getKey(), entry.getValue() / sum
                ); //if sum is 0, then we do not even do this
                score.add(pair);
            }
            result.put(info.e, score);
        }

        LOG.debug("Finished ObjectAssigner heuristic ranking.");

        // Return the value
        return result;
    }

    @Override
    public void learn() {
        LOG.debug("Starting ObjectAssigner learning.");

        //Create the train model
        this.train = new TrainWeka();

        //Train the model
        this.model = train.doTraining(this.objectTableDAO, this.aliasTableDAO);

        LOG.debug("Finished ObjectAssigner learning.");
    }

    @Override
    public Map<Entity, List<Pair<Long, Double>>> machineLearningRanking(
            final String document,
            final List<Entity> entities,
            final int topK) {

        LOG.debug("Starting ObjectAssigner Weka machine learning ranking.");

        // Initialize the result
        final Map<Entity, List<Pair<Long, Double>>> result = new HashMap<>();

        // Machine learning ranking
        for (Entity entity : entities) {
            final List<ObjectTable> objects = getCloseObjects(entity);
            final BoundedPriorityQueue<Pair<Long, Double>> scores =
                    new BoundedPriorityQueue<>(topK, (p1, p2) -> {
                        return p1.getSecond().compareTo(p2.getSecond());
                    }); //already selects only topK items

            for (ObjectTable object : objects) {
                // Everything is positive , it does not matter
                Instance ins = train.CreateInstanceBasic(entity, object, aliasTableDAO, objectTableDAO, "positive");
                ins.setDataset(train.isTrainingSet);

                // Assign value
                try {
                    final double[] fDistribution =
                            model.distributionForInstance(ins);
                    final double score = fDistribution[0];
                    final Pair<Long, Double> probability =
                            new Pair<>(object.getId(), score);
                    scores.add(probability);
                } catch (Exception ex) {
                    LOG.error("Machine learning ranking failed: " + ex.getMessage());
                }
            }

            // Return
            result.put(entity, new ArrayList<>(scores.getQueue()));
        }
        LOG.debug("Finishing ObjectAssigner Weka machine learning ranking.");
        return result;
    }
}
