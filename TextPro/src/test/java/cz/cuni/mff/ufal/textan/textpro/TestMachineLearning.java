/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author HOANGT
 */
public class TestMachineLearning {

    /*
     Test the ranking and learning
     */
    @Test
    public void TestRanking() {
        IAliasOccurrenceTableDAO aliasOccurrenceTableDAO = new AliasOccurrenceTableDAO();
        IRelationTypeTableDAO typeTableDAO = new RelationTypeTableDAO();
        IAliasTableDAO aliasTableDAO = new AliasTableDAO();
        IJoinedObjectsTableDAO joinedObjectsTableDAO = new JoinedObjectsTableDAO();
        IObjectTableDAO objectTableDAO = new ObjectTableDAO();
        IObjectTypeTableDAO objectTypeTableDAO = new ObjectTypeTableDAO();
        IRelationOccurrenceTableDAO relationOccurrenceTableDAO = new RelationOccurrenceTableDAO();
        IRelationTableDAO relationTableDAO = new RelationTableDAO();
        
        ITextPro tp = new TextPro(aliasOccurrenceTableDAO, typeTableDAO, aliasTableDAO, 
                                  joinedObjectsTableDAO, objectTableDAO, objectTypeTableDAO, 
                                   relationOccurrenceTableDAO, relationTableDAO);
        tp.learn();
        Entity e = new Entity("Emily", 0, 0 , "Person");
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());
        Map<Entity, Map<Long, Double>> result = tp.DoubleRanking("Empty", eList, 5);
        assertEquals("1 entity to match", 1, result.keySet().size());
    }
}
