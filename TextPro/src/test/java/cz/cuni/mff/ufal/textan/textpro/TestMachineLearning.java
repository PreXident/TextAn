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
    ITextPro textPro;

    /*
     Test the ranking and learning
     */
    @Test
    public void TestRanking() {
        // Learn from database
        textPro.learn();
        
        // Create fake test
        Entity e = new Entity("Emily", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());
        
        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.DoubleRanking("Empty", eList, 5);
        
        List<Pair<Long, Double>> Olist = result.get(e);
        //assertEquals("1 entity to match", 0, result.keySet().size());
        //assertEquals("2 one object", 0, Olist.size());
        //assertEquals("2 zero object", 1, Olist.keySet().size());
    }
    
    @Test
    public void TestGetCloseObject() {
        // Learn from database
        textPro.learn();
        
        // Create fake test
        Entity e = new Entity("Emily", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        List<ObjectTable> oList = objectTableDAO.findAllByAliasSubstring(e.getText());
        //assertEquals("1 objst to find close", 1, oList.size());   
    }
    
}
