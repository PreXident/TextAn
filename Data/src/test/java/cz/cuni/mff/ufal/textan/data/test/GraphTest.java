/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.graph.Graph;
import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.tables.InRelationTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 *
 * @author Václav Pernička
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class}, loader = AnnotationConfigContextLoader.class)
public class GraphTest {

    @Autowired
    private Data data;
    
    @Autowired
    private GraphFactory graphFactory;

    static final int OBJECTS_COUNT = 20;
    static final int OBJECTS_IN_RELATION_COUNT = 10;
    static final String TEST_PREFIX = "[TEST][GRAPH] ";
    
    static ObjectTypeTable objectType = new ObjectTypeTable(TEST_PREFIX + "object type 1");
    static RelationTypeTable relationType = new RelationTypeTable(TEST_PREFIX + "relation type 1");
    static RelationTable relation = new RelationTable(relationType);
    
    static ObjectTable[] objects = new ObjectTable[OBJECTS_COUNT];
    static InRelationTable[] inRelation = new InRelationTable[OBJECTS_IN_RELATION_COUNT];
    
    public GraphTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        System.out.println("SETUP");
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new ObjectTable(TEST_PREFIX + "object numero " + i, objectType);
            data.addRecord(objects[i]);
        }
        data.addRecord(relation);
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            inRelation[i] = new InRelationTable(i, relation, objects[i]);
            data.addRecord(inRelation[i]);
        }
    }
    
    @After
    public void tearDown() {
        System.out.println("\n\nTEAR DOWN");
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            data.deleteRecord(inRelation[i]);
        }
        for (int i = 0; i < objects.length; i++) {
            data.deleteRecord(objects[i]);
        }
        
        data.deleteRecord(objectType);
        data.deleteRecord(relation);
        data.deleteRecord(relationType);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void basicGraphTest() {
        System.out.println("\n\nbasicGraphTest");
        Graph g = null;//graphFactory.getGraphFromObject(objects[0].getId(), 1);
        System.out.println(g);
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            assertTrue("Object not in graph: i = " + i, g.getNodes().contains(new ObjectNode(objects[i])));
        }
    }
}
