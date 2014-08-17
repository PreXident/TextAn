/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasOccurrenceTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationOccurrenceTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTypeTableDAO;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class TestRanking {
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

    @Test
    public void TestRank() {
        // Create fake test
        Entity e = new Entity("Emily", 0, 0 , 1);
        List<Entity> eList = new ArrayList<>();
        eList.add(e);
        assertEquals("1 entity to match", 1, eList.size());
        
        // Run the ranking
        Map<Entity, List<Pair<Long, Double>>> result = textPro.HeuristicRanking("Empty", eList, 5);
        
        List<Pair<Long, Double>> Olist = result.get(e);
        assertEquals("1: entity to match", 1, result.keySet().size());
        assertEquals("2: one object found", 1, Olist.size());
        //assertEquals("2 zero object", 1, Olist.keySet().size());
    }
    
}
