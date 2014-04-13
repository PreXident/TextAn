package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * A simple example of an implementation of the ITextPro interface as a Spring bean.
 * @author Petr Fanta
 * @see cz.cuni.mff.ufal.textan.textpro.ITextPro
 */
public class TextPro implements ITextPro {

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

    /** List of Entity and the Objects which related to Entity **/
    Map<Entity, List<ObjectTable>> mapping;

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

    //TODO: implement or change interface method

    @Override
    public void init(List<String> objectlist) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Entity> recognizedEntity(String document) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Entity> SimpleRanking(String TestDir, String DataDir) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     *
     * @return the result of DoubleRank
     */
    @Override
    public Map<Entity, Map<Long, Double>> DoubleRanking(String document,List<Entity> eList){
        /*
         * Assign value to the mapping
         */
        //mappingSetter(eList);
        
        // Initialize the eMap - final result
        Map<Entity, Map<Long, Double>> eMap = new HashMap<Entity, Map<Long, Double>>();
        for (int id = 0; id < eList.size(); id++) {
            Entity e = eList.get(id);
            List<ObjectTable> oList = getCloseObject(e);
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
            for (int i = 0; i < size; i++) {
                sum+= score[i];
            }
            for (int i = 0; i < size; i++) {
                score[i] = score[i]/sum;
            }
                        
            /* Assign value */
            Map <Long,Double> entityScore = new HashMap <Long,Double>();
            for (int i = 0; i < size; i++){
                entityScore.put(oListID.get(id), score[i]);
            }
            eMap.put(e, entityScore);
            
        }
        // Return the value
        return eMap;
        
        //throw new UnsupportedOperationException("Not implemented yet");
        /// waiting for the double rank
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
    
    public void mappingSetter(List<Entity> eList){
        Map<Entity, List<ObjectTable>> eMap = new HashMap<Entity, List<ObjectTable>>();
        for(int id = 0; id < eList.size(); id ++) {
            Entity e = eList.get(id);
            eMap.put(e, getCloseObject(e));
        }
        this.mapping = eMap;
    }

}
