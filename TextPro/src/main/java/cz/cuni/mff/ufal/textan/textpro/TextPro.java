package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.EntityInfo;
import cz.cuni.mff.ufal.textan.textpro.learning.Test;
import cz.cuni.mff.ufal.textan.textpro.learning.Train;
import cz.cuni.mff.ufal.textan.textpro.learning.TrainWeka;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.Classifier;
import weka.core.Instance;

/**
 * A simple example of an implementation of the ITextPro interface as a Spring bean.
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.textpro.ITextPro
 * @author Tam Hoang
 * Implement the first ranking scheme DoubleRanking(document, list of entities, number of K)
 */
@Transactional
public class TextPro implements ITextPro {

    private static Logger LOG = LoggerFactory.getLogger(TextPro.class);

    /** Provides access to AliasOccurrence table in database */
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    /** Provides access to Alias table in database */
    IAliasTableDAO aliasTableDAO;

    /** Provides access to JoinedObjects table in database */
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    /** Provides access to ObjectTable table in database */
    IObjectTableDAO objectTableDAO;

    /** Provides access to ObjectType table in database */
    IObjectTypeTableDAO objectTypeTableDAO;

    /** Provides access to RelationOccurrence table in database */
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    /** Provides access to Relation table in database */
    IRelationTableDAO relationTableDAO;

    /** Provides access to RelationType table in database */
    IRelationTypeTableDAO typeTableDAO;
    
    /*8 Train Weka */
    TrainWeka train;
    /** Training model **/
    Classifier model ;
    
    
    /**
     * Instantiates a new TextPro.
     * Uses a constructor injection for an initialization of data access object ({@link cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig#textPro()}
     *
     * @param aliasOccurrenceTableDAO the alias occurrence table DAO
     * @param typeTableDAO the type table DAO
     * @param aliasTableDAO the alias table DAO
     * @param joinedObjectsTableDAO the joined objects table DAO
     * @param objectTableDAO the object table DAO
     * @param objectTypeTableDAO the object type table DAO
     * @param relationOccurrenceTableDAO the relation occurrence table DAO
     * @param relationTableDAO the relation table DAO
     */
    public TextPro(
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTypeTableDAO typeTableDAO,
            IAliasTableDAO aliasTableDAO,
            IJoinedObjectsTableDAO joinedObjectsTableDAO,
            IObjectTableDAO objectTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO,
            IRelationTableDAO relationTableDAO) {

        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
        this.typeTableDAO = typeTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.relationOccurrenceTableDAO = relationOccurrenceTableDAO;
        this.relationTableDAO = relationTableDAO;
        
    }
    
    @Override
    public void learn() {
        LOG.debug("Starting TexPro learning.");

        /*** Create the train model**/
        this.train = new TrainWeka();
        
        /*** Train the model **/
        this.model = train.doTraining(this.objectTableDAO, this.aliasTableDAO);

        LOG.debug("Finished TexPro learning.");
    }
    //TODO: implement or change interface method


    @Override
    public List<String> TokenizeDoc(String document) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Double ranking is the main function of TextPro
     * It takes the input as a documents, a list of entity and the number of wanted result
     * It produces the output as a Map between the entity and the id of object, along with it scores
     * MAP<Entity, Map<Object ID, Score>>
     * @param document
     * @param eList
     * @return the result of DoubleRank
     */
    @Override
    //public Map<Entity, Map<Long, Double>> DoubleRanking(String document, List<Entity> eList, int topK){
    public Map<Entity, List<Pair<Long, Double>>> DoubleRanking(String document, List<Entity> eList, int topK){

        LOG.debug("Starting TexPro ranking.");

        /*
         * Assign value to the mapping
         */
        /********************** REGULAR RANKING **************************/
        // Initialize the eMap - final result
        Map<Entity, List<Pair<Long, Double>>> eMap = new HashMap<>();

        for (int id = 0; id < eList.size(); id++) {
            /********************** REGULAR RANKING **************************/
            Entity e = eList.get(id);
            List<ObjectTable> oList = getCloseObject(e); // List of object closed to the entity
            List<Long> oListID = getCloseObjectID(e);
            
            //List<Double> score = new ArrayList<Double>();
            /** Initialize all value is 1 for one matching **/
            Double[] score = new Double[oList.size()];
            int size = 0;
            for(ObjectTable o: oList) {
                score[size] = 1.0;
                size++; // funny way to loop :)
            }
            
            /* Increate the score of value if they share the same object */ 
            for(Entity e_other : eList) {
                if(e_other.equals(e)){
                    continue;
                }
                List<Long> oListID_other = getCloseObjectID(e_other);
                for(int id_o = 0; id_o < oList.size(); id_o++) {
                    if(oListID_other.indexOf(oList.get(id_o)) != -1) {
                        score[id_o] += 1.0;
                    }
                }
            }
            
            /* Normalize the value */
            double sum = 0;
            double minscore = 0; // the minimum value of score will be taken
            for (int i = 0; i < size; i++) {
                sum+= score[i];
                
            }
            for (int i = 0; i < size; i++) {
                score[i] = score[i]/sum;
            }
            if(size > topK) {
                Double[] sort_score= score.clone();
                minscore = sort_score[topK];
            }
            /********************** MACHINE LEARNING **************************/
            
            /* Get the test list */
            
            Test test = new Test(e, oList, oListID, score, minscore);
            
            /* Running the classifier, but it is included in the assigning value already
            for(Instance in:instances){
                Object predictedClassValue = ml.classify(in);
                System.out.println("Predict: " + predictedClassValue.toString());
            }
            */
            /* Assign value */
            /***************** ASSIGN VALUE *********************************/
            List<Pair<Long, Double>> entityScore = new ArrayList<>();
            for (int test_id = 0; test_id < test.getObjectListID().size();  test_id++){
                if(true) {
                    entityScore.add(new Pair<>(test.getObjectListID().get(test_id), test.getObjectListScore().get(test_id)));
                }
            }
            eMap.put(e, entityScore);
        }

        LOG.debug("Finished TexPro ranking.");

        // Return the value
        return eMap;
    }
    
    public List<ObjectTable> getCloseObject(Entity e){
        return this.objectTableDAO.findAllByAliasSubstring(e.getText());
    }
    
    public ArrayList<Long> getCloseObjectID(Entity e){
        List<ObjectTable> oList =  getCloseObject(e);
        ArrayList<Long> ID = new ArrayList<Long>();
        for(ObjectTable o:oList) {
            ID.add(o.getId());
        }
        return ID;
    }
    
    
    /*
    * Interpolate ranking make use of Weka, not JavaML
    */
    @Override
    public Map<Entity, List<Pair<Long, Double>>> InterpolateRanking(String document, List<Entity> eList, int topK) {
        
        LOG.debug("Starting TexPro weka ranking.");

        // Initialize the eMap - final result
        Map<Entity, List<Pair<Long, Double>>> eMap = new HashMap<>();
        
        // Initialize the list of entity info
        List<EntityInfo> eInfoList = new ArrayList<>();
        for (Entity e:eList){
            
            List<Long> oListID = getCloseObjectID(e);
            Map<Long,Double> score = new HashMap<Long,Double>();
            Map<ObjectTable,Long> match = new HashMap<ObjectTable,Long>();
            List<ObjectTable> oList = getCloseObject(e);

            // Initialize 
            for(int oID = 0; oID < oListID.size(); oID++){
                long objectID = oListID.get(oID);
                ObjectTable object = oList.get(oID);
                score.put(objectID, 1.0);
                match.put(object, objectID);
            }
            
            // Add the current info to final list
            eInfoList.add(new EntityInfo(e, score, oList, match));
            
        }
        
        /********************** REGULAR RANKING **************************/
        
        /********************** MACHINE LEARNING *************************/
        
        for(EntityInfo eInfo:eInfoList){
            List<Pair<Long, Double>> entityScore = new ArrayList<>();
            for(ObjectTable ot:eInfo.objects){
                // Everything is positive , it does not matter
                Instance ins = train.CreateInstanceBasic(eInfo.e, ot, aliasTableDAO,objectTableDAO, "positive");
                ins.setDataset(train.isTrainingSet);
                
                // Assign value
                double score = 0.0;
                try {
                    double[] fDistribution = model.distributionForInstance(ins);
                    score = fDistribution[0];
                } catch (Exception ex) {
                    System.out.println("Something wrong here" + ex.getMessage());
                }
                
                Pair probability = new Pair<>(eInfo.match_object.get(ot),score);
                entityScore.add(probability);
                
            }
            eMap.put(eInfo.e, entityScore);
        }
        
        return eMap;
    }

    
    
}
