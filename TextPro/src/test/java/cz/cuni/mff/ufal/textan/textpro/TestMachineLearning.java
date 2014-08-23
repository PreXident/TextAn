/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
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
 *
 * @author HOANGT
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TextProConfig.class}, loader = AnnotationConfigContextLoader.class)
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
    ITextPro textPro;

    /*
     Test the learning
     */
    @Test
    public void TestML() {
        // Learn from database
        textPro.learn();
        
        // Create fake test
        Entity e = new Entity("Emily", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());
        
        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.machineLearning("Empty", eList, 5);
        
        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 2, result.keySet().size());
        assertEquals("2: two object found", 2, Olist.size());
        //assertEquals("2 zero object", 1, Olist.keySet().size());
    }
    
    @Test
    public void TestGetCloseObject() {
        // Learn from database
        //textPro.learn();
        
        // Create fake test
        Entity e = new Entity("Ema", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        List<ObjectTable> oListFullText = objectTableDAO.findAllByObjTypeAndAliasFullText(e.getType(), e.getText());
        //List<ObjectTable> oListSubStr = objectTableDAO.findAllByObjectTypeAndAliasSubStr(e.getType(), e.getText());
        
        assertEquals("1 object matched full text", 1, oListFullText.size());   
        //assertEquals("2 objects matched sub string", 2, oListSubStr.size());   
        
    }
    
    /*
     Test the ranking and learning
     */
    @Test
    public void TestFinalRanking() {
        // Learn from database
        textPro.learn();
        
        // Create fake test
        Entity e = new Entity("Ema", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        
        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.finalRanking("Empty", eList, 5);
        // If everything is alright, it will call both ML and HR
        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 2, result.keySet().size());
        assertEquals("2: one object found", 2, Olist.size());
    }
    
}
