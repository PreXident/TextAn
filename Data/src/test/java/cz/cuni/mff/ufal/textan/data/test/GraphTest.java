/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.graph.Graph;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
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

/**
 *
 * @author Václav Pernička
 */
public class GraphTest {
    
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
        System.out.println("SETUP");
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new ObjectTable(TEST_PREFIX + "object numero " + i, objectType);
            DataSingleton.getSingleton().addRecord(objects[i]);
        }
        DataSingleton.getSingleton().addRecord(relation);
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            inRelation[i] = new InRelationTable(i, relation, objects[i]);
            DataSingleton.getSingleton().addRecord(inRelation[i]);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("\n\nTEAR DOWN");
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            DataSingleton.getSingleton().deleteRecord(inRelation[i]);
        }
        for (int i = 0; i < objects.length; i++) {
            DataSingleton.getSingleton().deleteRecord(objects[i]);
        }
        
        DataSingleton.getSingleton().deleteRecord(objectType);
        DataSingleton.getSingleton().deleteRecord(relation);
        DataSingleton.getSingleton().deleteRecord(relationType);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void basicGraphTest() {
        System.out.println("\n\nbasicGraphTest");
        Graph g = Graph.getGraphFromObject(objects[0].getId(), 1);
        System.out.println(g);
        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            assertTrue("Object not in graph: i = " + i, g.getNodes().contains(new ObjectNode(objects[i])));
        }
    }
}
