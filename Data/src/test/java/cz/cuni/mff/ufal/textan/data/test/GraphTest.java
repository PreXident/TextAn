/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.exceptions.PathDoesNotExistException;
import cz.cuni.mff.ufal.textan.data.graph.Graph;
import cz.cuni.mff.ufal.textan.data.graph.GraphFactory;
import cz.cuni.mff.ufal.textan.data.graph.ObjectNode;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.data.test.common.Data;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Vaclav Pernicka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, Data.class}, loader = AnnotationConfigContextLoader.class)
public class GraphTest {

    static final int OBJECTS_COUNT = 20;
    static final int OBJECTS_IN_RELATION_COUNT = 10;
    static final String TEST_PREFIX = "[TEST][GRAPH] ";
    static final ObjectTable[] objects = new ObjectTable[OBJECTS_COUNT];
    static final InRelationTable[] inRelation = new InRelationTable[OBJECTS_IN_RELATION_COUNT];
    static ObjectTypeTable objectType;
    static RelationTypeTable relationType;
    static RelationTable relation;
    @Autowired
    private Data data;
    @Autowired
    private GraphFactory graphFactory;

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

        objectType = new ObjectTypeTable(TEST_PREFIX + "object type 1");
        relationType = new RelationTypeTable(TEST_PREFIX + "relation type 1");
        relation = new RelationTable(relationType);

        data.addRecord(objectType);
        data.addRecord(relationType);

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
        for (ObjectTable object : objects) {
            data.deleteRecord(object);
        }

        data.deleteRecord(objectType);
        data.deleteRecord(relation);
        data.deleteRecord(relationType);
    }

    @Test
    public void basicGraphSingleNodeTest() {
        System.out.println("\n\nbasicGraphSingleNodeTest");
        ObjectTable singleObject = objects[OBJECTS_IN_RELATION_COUNT];

        Graph g = graphFactory.getGraphFromObject(singleObject, 1);
        System.out.println(g);
        assertEquals("There is not correct count of nodes in Graph", 1, g.getNodes().size());
        assertTrue("There is not correct node in Graph", g.getNodes().contains(new ObjectNode(singleObject)));

        assertEquals("There is not correct count of edges in Graph ", 0, g.getEdges().size());

    }

    @Test
    public void basicGraphTest() {
        System.out.println("\n\nbasicGraphTest");
        Graph g = graphFactory.getGraphFromObject(objects[0].getId(), 1);
        System.out.println(g);
        assertEquals("There is not correct count of nodes in Graph", OBJECTS_IN_RELATION_COUNT + 1, g.getNodes().size());

        for (int i = 0; i < OBJECTS_IN_RELATION_COUNT; i++) {
            assertTrue("Object not in graph: i = " + i, g.getNodes().contains(new ObjectNode(objects[i])));
        }
    }

    @Test
    public void pathFromAtoATest0() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest0");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[0], 0);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test
    public void pathFromAtoATest1() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest1");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[0], 1);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test
    public void pathFromAtoATest2() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest2");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[0], 2);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test(expected = PathDoesNotExistException.class)
    public void pathFromAtoBTest0() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest0");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[1], 0);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test
    public void pathFromAtoBTest1() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest1");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[1], 1);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test
    public void pathFromAtoBTest2() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest2");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[1], 2);
        System.out.println(g);
        assertTrue("Start Node is not in result", g.getNodes().contains(new ObjectNode(objects[0])));
    }

    @Test
    public void pathFromAtoBNotCTest2() throws PathDoesNotExistException {
        System.out.println("\n\npathFromAtoATest2");
        Graph g = graphFactory.getShortestPathBetweenObjects(objects[0], objects[1], 2);
        System.out.println(g);
        assertTrue("There is a node in result that is not on the path",
                !g.getNodes().contains(new ObjectNode(objects[2])));
    }
}
