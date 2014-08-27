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
 * Test class for machine learning ranking.
 * @author HOANGT
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectAssignerConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TestMachineLearning {

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
     * Test the machine learning ranking.
     */
    @Test
    public void TestML() {
        // Learn from database
        textPro.learn();

        // Create fake test
        Entity e = new Entity("Emily", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());

        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.machineLearningRanking("Empty", eList, 5);

        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 1, result.keySet().size());
        assertEquals("2: two object found", 0, Olist.size());
        //assertEquals("2 zero object", 1, Olist.keySet().size());
    }

    /**
     * TODO this does not call close objects! How can it test it?
     */
    @Test
    public void TestGetCloseObject() {
        // Learn from database
        //textPro.learn();

        // Create fake test
        Entity e = new Entity("Em", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        List<ObjectTable> oListFullText = objectTableDAO.findAllByObjTypeAndAliasFullText(e.getType(), e.getText());
        List<ObjectTable> oListSubStr = objectTableDAO.findAllByObjectTypeAndAliasSubStr(e.getType(), e.getText());

        assertEquals("1 object matched full text", 0, oListFullText.size());
        assertEquals("2 objects matched sub string", 2, oListSubStr.size());

    }

    /**
     * Test the whole process.
     */
    @Test
    public void TestFinalRanking() {
        // Learn from database
        textPro.learn();

        // Create fake test
        Entity e = new Entity("Ema", 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);

        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.createObjectRanking("Empty", eList, 5);
        // If everything is alright, it will call both ML and HR
        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 1, result.keySet().size());
        assertEquals("2: one object found", 2, Olist.size());
    }

}
