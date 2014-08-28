package cz.cuni.mff.ufal.textan.assigner;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.assigner.configs.ObjectAssignerConfig;
import cz.cuni.mff.ufal.textan.assigner.data.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * @author HOANGT
 * Warnings: The test results depend on the actual data in the database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectAssignerConfig.class}, loader = AnnotationConfigContextLoader.class)
public class ObjectAssignerTest {

    @Autowired
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    @Autowired
    IAliasTableDAO aliasTableDAO;

    @Autowired
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    @Autowired
    IObjectTableDAO objectTableDAO;

    @Autowired
    IObjectTypeTableDAO objectTypeTableDAO;

    @Autowired
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    @Autowired
    IRelationTableDAO relationTableDAO;

    @Autowired
    IRelationTypeTableDAO typeTableDAO;

    @Autowired
    IDocumentTableDAO documentTableDAO;

    @Autowired
    IObjectAssigner textPro;

    /**
     * Test the machine learning, include learn/assign
     */
    @Test
    public void TestMachineLearning() {
        // Learn from database
        textPro.learn();

        // Create an artificial test which is not in database
        Entity e = new Entity("Emily", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());

        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.machineLearningRanking("", eList, 5);

        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 1, result.keySet().size());
        assertEquals("2: two object found", 0, Olist.size());
    }

    /**
     * Problem: To test getCloseObjectFunction, we have to make it to the interface, which I don't want.
     */
    @Test
    public void TestFindAllObject() {
        
        // create a list of entity
        List<Entity> eList = new ArrayList<>();
        
        // Create an artificial test
        Entity e1 = new Entity("Juchavice", 1);
        List<ObjectTable> oListFullText1 = objectTableDAO.findAllByObjTypeAndAliasFullText(e1.getType(), e1.getText());
        List<ObjectTable> oListSubStr1 = objectTableDAO.findAllByObjectTypeAndAliasSubStr(e1.getType(), e1.getText());
        
        assertEquals("1 object matched full text search", 1, oListFullText1.size());
        assertEquals("1 object matched sub string search", 1, oListSubStr1.size());
        
        
        // Create an artificial test
        Entity e2 = new Entity("Em", 1);
        List<ObjectTable> oListFullText2 = objectTableDAO.findAllByObjTypeAndAliasFullText(e2.getType(), e2.getText());
        List<ObjectTable> oListSubStr2 = objectTableDAO.findAllByObjectTypeAndAliasSubStr(e2.getType(), e2.getText());
        assertEquals("1 object matched full text", 0, oListFullText2.size());
        assertEquals("2 objects matched sub string", 2, oListSubStr2.size());
        
    }
    
    /*
    * Test the heuristic ranking schema
    */
    @Test
    public void TestHeuristicRanking() {
        // Create an artificial test which matched two objects
        Entity e = new Entity("Ema", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity in the list", 1, eList.size());

        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.heuristicRanking("Empty", eList, 5);

        List<Pair<Long, Double>> Olist = result.get(e);

        // Number of entity to match
        assertEquals("1: entity to match", 1, result.keySet().size());

        //number of object matched the entity e
        assertEquals("2: one object found", 2, Olist.size());
    }
    
    /**
     * Test the combination of heuristic ranking and machine learning
     */
    @Test
    public void TestCombinedObjectRanking() {
        // Learn from database
        textPro.learn();

        // Create fake test
        Entity e = new Entity("Ema", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);

        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.combinedObjectRanking("Empty", eList, 5);
        // If everything is alright, it will call both ML and HR
        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 1, result.keySet().size());
        assertEquals("2: one object found", 2, Olist.size());
    }

    
}
