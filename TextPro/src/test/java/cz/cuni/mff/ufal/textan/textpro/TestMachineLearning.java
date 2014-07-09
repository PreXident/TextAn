/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.*;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        Map<Entity, Object> result = textPro.DoubleRanking("Empty", eList, 5);
        assertEquals("1 entity to match", 1, result.keySet().size());
    }
}
