package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.EntityInfo;
import cz.cuni.mff.ufal.textan.textpro.learning.TrainWeka;

import java.util.*;
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
 * Implement the ranking scheme HeuristicRanking(document, list of entities, number of K)
 * Implement the machine learning scheme MachineLearning 
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
    
    /** Provides access to DocumentTable table in database */
    IDocumentTableDAO documentTableDAO;
    
    /* Train Weka */
    TrainWeka train;
    /** Training model **/
    Classifier model ;
    
    
    /**
     * Instantiates a new TextPro.
     * Uses a constructor injection for an initialization of data access object ({@link cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig#textPro(cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasOccurrenceTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTypeTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO, cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationOccurrenceTableDAO)}
     *
     * @param aliasOccurrenceTableDAO the alias occurrence table DAO
     * @param typeTableDAO the type table DAO
     * @param aliasTableDAO the alias table DAO
     * @param joinedObjectsTableDAO the joined objects table DAO
     * @param objectTableDAO the object table DAO
     * @param objectTypeTableDAO the object type table DAO
     * @param relationOccurrenceTableDAO the relation occurrence table DAO
     * @param relationTableDAO the relation table DAO
     * @param documentTableDAO
     */
    public TextPro(
            IAliasOccurrenceTableDAO aliasOccurrenceTableDAO,
            IRelationTypeTableDAO typeTableDAO,
            IAliasTableDAO aliasTableDAO,
            IJoinedObjectsTableDAO joinedObjectsTableDAO,
            IObjectTableDAO objectTableDAO,
            IObjectTypeTableDAO objectTypeTableDAO,
            IRelationOccurrenceTableDAO relationOccurrenceTableDAO,
            IRelationTableDAO relationTableDAO,
            IDocumentTableDAO documentTableDAO) {

        this.aliasOccurrenceTableDAO = aliasOccurrenceTableDAO;
        this.typeTableDAO = typeTableDAO;
        this.aliasTableDAO = aliasTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
        this.objectTableDAO = objectTableDAO;
        this.objectTypeTableDAO = objectTypeTableDAO;
        this.relationOccurrenceTableDAO = relationOccurrenceTableDAO;
        this.relationTableDAO = relationTableDAO;
        this.documentTableDAO = documentTableDAO;
        
    }
    
    /*
    * Learn function: Run machine learning and build the model from database
    */
    @Override
    public void learn() {
        LOG.debug("Starting TexPro learning.");

        /*** Create the train model**/
        this.train = new TrainWeka();
        
        /*** Train the model **/
        this.model = train.doTraining(this.objectTableDAO, this.aliasTableDAO);

        LOG.debug("Finished TexPro learning.");
    }

    /*
    * TokenizeDoc: split the document into a list of tokens
    * For now: not needed yet.
    */
    @Override
    public List<String> TokenizeDoc(String document) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * HeuristicRanking: Run a simple ranking schema to get the reusult
     * It takes the input as a documents, a list of entity and the number of wanted result
     * It produces the output as a Map between the entity and the id of object, along with it scores
     * @param document
     * @param eList
     * @param topK
     * @return the result of DoubleRank
     */
    @Override
    public Map<Entity, List<Pair<Long, Double>>> HeuristicRanking(String document, List<Entity> eList, int topK){

        LOG.debug("Starting TexPro ranking.");

        /********************** REGULAR RANKING **************************/
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
        for(EntityInfo eInfo:eInfoList){
            //List<Pair<Long, Double>> entityScore = new ArrayList<>();
            Map<Long,Double> score = eInfo.score;
            
            String eText = eInfo.e.getText();
            
            // Repeatitive update the score
            for(EntityInfo eOtherInfo:eInfoList){
                String eOtherText = eOtherInfo.e.getText();
                if(eText.equalsIgnoreCase(eOtherText)) {
                    continue; // Check if they have the same text, not just same entity
                }
                for(ObjectTable oTable:eInfo.objects) {
                    for(ObjectTable oOtherTable:eOtherInfo.objects){
                        if(checkRelation(oTable, oOtherTable)){
                            // Increate the score by 1
                            score.put(oTable.getId(), score.get(oTable.getId()) + 1.0);
                        }
                    }
                }
            }
            
            // Select topK
            List<Pair<Long, Double>> entityScoreTopK = new ArrayList<>();
            Set<Long>entityTopK = new HashSet<>();
            for(int iteration = 0; iteration < topK; iteration++) {
                double highestScore = 0;
                long highestID = -1;
                for(long thisID:score.keySet()) {
                    if(entityTopK.contains(thisID)) {
                        continue;
                    }
                    double thisScore = score.get(thisID);
                    if(thisScore > highestScore){
                        highestScore = thisScore;
                        highestID = thisID;
                    }
                }
                if(highestID > -1) {
                    entityTopK.add(highestID);
                    entityScoreTopK.add(new Pair<>(highestID,highestScore));
                }
            }
            
            // Normalize TopK and add to value
            List<Pair<Long, Double>> entityScoreTopKNormalize = new ArrayList<>();
            double sum = 0;
            for(Pair p:entityScoreTopK){
                sum += (double)p.getSecond();
            }
            if(sum <= 0){
                eMap.put(eInfo.e, entityScoreTopK);
            } else {
                for(Pair p:entityScoreTopK){
                    entityScoreTopKNormalize.add(new Pair<>((long)p.getFirst(),((double)p.getSecond()/sum)));
                }
                eMap.put(eInfo.e, entityScoreTopKNormalize);
            }
        }
        
        LOG.debug("Finished TexPro ranking.");

        // Return the value
        return eMap;
    }
    /*
    * Machine Learning with Weka, not JavaML
    * Return the same kind of value as HeuristicRanking
    */
    @Override
    public Map<Entity, List<Pair<Long, Double>>> MachineLearning(String document, List<Entity> eList, int topK) {
        
        LOG.debug("Starting TexPro weka learning.");

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
            
            // Select topK
            List<Pair<Long, Double>> entityScoreTopK = new ArrayList<>();
            Set<Long>entityTopK = new HashSet<>();
            for(int iteration = 0; iteration < topK; iteration++) {
                double highestScore = 0;
                long highestID = -1;
                for(Pair p:entityScore) {
                    long thisID = (long)p.getFirst();
                    if(entityTopK.contains(thisID)) {
                        continue;
                    }
                    double thisScore = (double)p.getSecond();
                    if(thisScore > highestScore){
                        highestScore = thisScore;
                        highestID = thisID;
                    }
                }
                if(highestID > -1) {
                    entityTopK.add(highestID);
                    entityScoreTopK.add(new Pair<>(highestID,highestScore));
                }
            }
            // Return
            eMap.put(eInfo.e, entityScoreTopK);
            
        }
        LOG.debug("Finishing TexPro weka learning.");
        return eMap;
    }

    /*
    * Check relationship between two object
    * Method: Check if they share any documents
    * If two objects happen to be in the same document, they have relation
    * If two objects does not share the document, they are not related
    */
    boolean checkRelation(ObjectTable o1, ObjectTable o2) {
        List<Pair<DocumentTable, Integer>> o1Docs = documentTableDAO.findAllDocumentsWithObject(o1);
        List<Pair<DocumentTable, Integer>> o2Docs = documentTableDAO.findAllDocumentsWithObject(o2);
        
        // Create a set of document
        Set<DocumentTable> o1DocsTable = new HashSet<DocumentTable>();
        for(Pair p:o1Docs){
            DocumentTable doc1 = (DocumentTable)p.getFirst();
            o1DocsTable.add(doc1);
        }
        for(Pair p:o2Docs){
            DocumentTable doc2 = (DocumentTable)p.getFirst();
            if(o1DocsTable.contains(doc2)) {
                return true;
            }
        }
        return false;
    }

    /*
    * getCloseObject: Get the object related to an entity by searching its alias
    */
    public List<ObjectTable> getCloseObject(Entity e){
        List<ObjectTable> matchFullText = this.objectTableDAO.findAllByAliasFullText(e.getText());
        if(matchFullText.isEmpty()){
            return this.objectTableDAO.findAllByAliasSubstring(e.getText());
        } 
        return matchFullText;
    }
    
    /*
    * getCloseObjectID: Get the object ID related to an entity by searching its alias
    */    
    public ArrayList<Long> getCloseObjectID(Entity e){
        List<ObjectTable> oList =  getCloseObject(e);
        ArrayList<Long> ID = new ArrayList<Long>();
        for(ObjectTable o:oList) {
            ID.add(o.getId());
        }
        return ID;
    }
    
    
}
