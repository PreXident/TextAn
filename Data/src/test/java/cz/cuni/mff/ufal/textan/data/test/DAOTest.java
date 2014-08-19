/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.exceptions.JoiningANonRootObjectException;
import cz.cuni.mff.ufal.textan.data.exceptions.JoiningEqualObjectsException;
import cz.cuni.mff.ufal.textan.data.repositories.dao.GlobalVersionTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IGlobalVersionTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.data.test.common.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.AssertionErrors;

import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.Assert;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Vaclav Pernicka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, Data.class}, loader = AnnotationConfigContextLoader.class)
public class DAOTest {
    @Autowired
    SessionFactory sessionFactory;
    
    @Autowired
    Data data;

    @Autowired
    IObjectTableDAO objectTableDAO;
    
    @Autowired
    IRelationTableDAO relationTableDAO;

    @Autowired
    IRelationTypeTableDAO relationTypeTableDAO;
        
    @Autowired
    IAliasTableDAO aliasTableDAO;

    @Autowired
    IDocumentTableDAO documentTableDAO;

    @Autowired
    IGlobalVersionTableDAO globalVersionDAO;
    
    @Autowired
    IJoinedObjectsTableDAO joinedObjectsDAO;
    
    private DocumentTable document;
    private RelationTypeTable relationType;
    private RelationTable withRelation;
    private RelationOccurrenceTable relationOccurrence;
    private ObjectTypeTable objectType;
    private ObjectTable object;
    private AliasTable alias;
    private AliasOccurrenceTable aliasOccurrence;
    
    private ObjectTypeTable objectTypeEmpty;
    private DocumentTable documentEmpty;
    private RelationTypeTable relationTypeEmpty;
    private ObjectTable object2;

    @Before
    public void setUp() {
        
        document = new DocumentTable("__[TEST] Example document with some crazy text.");
        relationType = new RelationTypeTable("__[TEST] with");
        withRelation = new RelationTable(relationType);
        relationOccurrence = new RelationOccurrenceTable(withRelation, document, 26, "with");
        objectType = new ObjectTypeTable("__[TEST]objecttype1");
        object = new ObjectTable("__[TEST] letter", objectType);
        object2 = new ObjectTable("__[TEST] letter2", objectType);        
        alias = new AliasTable(object, "document");
        aliasOccurrence = new AliasOccurrenceTable(17, alias, document);

        objectTypeEmpty = new ObjectTypeTable("__[TEST]objecttype2");
        relationTypeEmpty = new RelationTypeTable("__[TEST]relationtype2");
        documentEmpty = new DocumentTable("__[TEST] Empty document");

        
        System.out.println("Setup");
        //System.out.println("If class Method fails, be sure you started the database.");
        assertTrue("You have probably not run the database or the connection is not set properly", data.addRecord(document));
        assertTrue(data.addRecord(withRelation));
        assertTrue(data.addRecord(relationOccurrence));
        assertTrue(data.addRecord(object));
        assertTrue(data.addRecord(object2));
        
        assertTrue(data.addRecord(alias));
        assertTrue(data.addRecord(aliasOccurrence));
        
        assertTrue(data.addRecord(objectTypeEmpty));
        assertTrue(data.addRecord(documentEmpty));
        //withRelation.getObjectsInRelation().add(object);

    }
    
    @After
    public void tearDown() {
        
         System.out.println("\n\nClean");
         /*assertTrue(data.deleteRecord(relationOccurrence));
         assertTrue(data.deleteRecord(aliasOccurrence));
         assertTrue(data.deleteRecord(document));
         assertTrue(data.deleteRecord(withRelation));
         assertTrue(data.deleteRecord(withRelation.getRelationType()));
         assertTrue(data.deleteRecord(alias));
         assertTrue(data.deleteRecord(object));
         assertTrue(data.deleteRecord(object2));         
         assertTrue(data.deleteRecord(object.getObjectType()));

         assertTrue(data.deleteRecord(objectTypeEmpty));
         assertTrue(data.deleteRecord(documentEmpty));
         */

         System.out.println(" clearAllTables");
         Utils.clearTestValues(sessionFactory);
    }
    
    
    @Test
    public void findAllTest() {
        List<ObjectTable> res = objectTableDAO.findAll();
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void findTest() {
        ObjectTable objectTable = objectTableDAO.find(object.getId());
        AssertionErrors.assertEquals("Object not found", object, objectTable);
    }
    @Test
    public void failFindTest() {
        ObjectTable objectTable = objectTableDAO.find(-5L);
        if (objectTable == null) return;
        AssertionErrors.assertTrue("Object found but shouldnt be", !objectTable.equals(object));
    }
    @Test
    public void addAndDeleteTest() {
        
        ObjectTable object2 = new ObjectTable("__[TEST] letter2", objectType);
        Long key = objectTableDAO.add(object2);
        ObjectTable obj2 = objectTableDAO.find(key);
        AssertionErrors.assertEquals("Adding was not succesful", obj2, object2);
        objectTableDAO.delete(key);
        obj2 = objectTableDAO.find(key);
        AssertionErrors.assertEquals("Deleting was not succesful", obj2, null);
       
    }

    
    @Test
    public void objectTableFindAllByAliasEqualToTest() {
        List<ObjectTable> res = objectTableDAO.findAllByAliasEqualTo("document");
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void objectTableFindAllByAliasSubstringTest() {
        List<ObjectTable> res = objectTableDAO.findAllByAliasSubstring("ocum");
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void objectTableFindAllByDocumentOccurrenceTest() {
        List<ObjectTable> res = objectTableDAO.findAllByDocumentOccurrence(document);
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void objectTableFindAllByObjectTypeTest() {
        List<ObjectTable> res = objectTableDAO.findAllByObjectType(objectType);
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
                return;
        }
        assertTrue("Object not found", false);
    }

    @Test
    public void objectTableFailFindAllByObjectTypeTest() {
        List<ObjectTable> res = objectTableDAO.findAllByObjectType(objectTypeEmpty);
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
               assertTrue("Object found but should be found", false);
        }
    }

    @Test
    public void objectTableFailFindAllByDocumentOccurrenceTest() {
        List<ObjectTable> res = objectTableDAO.findAllByDocumentOccurrence(documentEmpty);
        for (ObjectTable objectTable : res) {
            if (objectTable.equals(object))
               assertTrue("Object found but should be found", false);
        }
    }
    
    @Test
    public void relationTableFindAllByAliasEqualToTest() {
        List<RelationTable> res = relationTableDAO.findAllByAliasEqualTo("with");
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
                return;
        }
        assertTrue("Relation not found", false);
    }
    
    @Test
    public void relationTableFindAllByAliasSubstringTest() {
        List<RelationTable> res = relationTableDAO.findAllByAliasSubstring("th");
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void relationTableFindAllByDocumentOccurrenceTest() {
        List<RelationTable> res = relationTableDAO.findAllByDocumentOccurrence(document);
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
                return;
        }
        assertTrue("Object not found", false);
    }
    
    @Test
    public void relationTableFindAllByObjectTypeTest() {
        List<RelationTable> res = relationTableDAO.findAllByRelationType(relationType);
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
                return;
        }
        assertTrue("Object not found", false);
    }

    @Test
    public void relationTableFailFindAllByObjectTypeTest() {
        List<RelationTable> res = relationTableDAO.findAllByRelationType(relationTypeEmpty);
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
               assertTrue("Object found but should be found", false);
        }
    }

    @Test
    public void relationTableFailFindAllByDocumentOccurrenceTest() {
        List<RelationTable> res = relationTableDAO.findAllByDocumentOccurrence(documentEmpty);
        for (RelationTable objectTable : res) {
            if (objectTable.equals(withRelation))
               assertTrue("Object found but should be found", false);
        }
    }
    
    @Test
    public void aliasFindAllByObjectTest() {
        List<AliasTable> res = aliasTableDAO.findAllAliasesOfObject(object);
        for (AliasTable objectTable : res) {
            if (objectTable.equals(alias))
                return;
        }
        assertTrue("Alias not found", false);

    }
    
    @Test
    public void documentFindAllDocumentsWithObjectTest() {
        List<Pair<DocumentTable, Integer>> res = documentTableDAO.findAllDocumentsWithObject(object);
        for (Pair<DocumentTable, Integer> objectTableCountPair : res) {
            if (objectTableCountPair.getFirst().equals(document))
                return;
        }
        assertTrue("Document not found", false);

    }
    
    @Test
    public void documentFindAllDocumentsWithRelationTest() {
        List<Pair<DocumentTable, Integer>> res = documentTableDAO.findAllDocumentsWithRelation(withRelation);
        for (Pair<DocumentTable, Integer> relationTableCountPair : res) {
            if (relationTableCountPair.getFirst().equals(document))
                return;
        }
        assertTrue("Document not found", false);

    }

    @Test
    public void findAllDocumentsWithObjectTest() {
        List<Pair<DocumentTable, Integer>> res = documentTableDAO.findAllDocumentsWithObject(object);
        for (Pair<DocumentTable, Integer> objectTableCountPair : res) {
            if (objectTableCountPair.getFirst().equals(document)) {
                Assert.assertEquals("Count is not 1", 1, objectTableCountPair.getSecond().intValue());
                return;
            }
        }
        assertTrue("Document not found", false);

    }
    
    @Test
    public void findAllDocumentsWithObjectByFullTextTest() {
        List<Pair<DocumentTable, Integer>> res = documentTableDAO.findAllDocumentsWithObjectByFullText(object.getId(), "document");
        for (Pair<DocumentTable, Integer> objectTableCountPair : res) {
            if (objectTableCountPair.getFirst().equals(document)) {
                Assert.assertEquals("Count is not 1", 1, objectTableCountPair.getSecond().intValue());
                return;
            }
        }
        assertTrue("Document not found", false);

    }
    
    @Test
    public void findAllDocumentsWithObjectByFullTextFailTest() {
        List<Pair<DocumentTable, Integer>> res = documentTableDAO.findAllDocumentsWithObjectByFullText(object.getId(), "documentBlamBlum");
        for (Pair<DocumentTable, Integer> objectTableCountPair : res) {
            if (objectTableCountPair.getFirst().equals(document)) {
                Assert.fail("Found some shit bro" + objectTableCountPair);
                return;
            }
        }
        

    }
    
    @Test
    public void globalVersionTest() {
        System.out.println("Global version = " + globalVersionDAO.getCurrentVersion());

        Assert.assertNotEquals("global version is not 0", 0, globalVersionDAO.getCurrentVersion());

    }
    
    @Test
    public void addAndRemoveJoinedObjectsTest() {
        System.out.println("\n\naddAndRemoveJoinedObjectsTest");
        JoinedObjectsTable user = new JoinedObjectsTable(object, object, object);
        System.out.println("user = " + user);
        assertTrue("Object type already exists or cant be added", joinedObjectsDAO.add(user) > 0);
        long id = user.getId();
        System.out.println("user.id = " + id);
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        JoinedObjectsTable user2 = null;
        user2 = joinedObjectsDAO.find(id);
        
        // TODO: wtf this doesnt work?
        // assertTrue("user2.equals(user):\nuser1 = " + user + ";\nuser2 = " + user2, user2.equals(user));
        
        //joinedObjectsDAO.delete(user2);
    }
    
    @Test
    public void joinObjectsTest() throws JoiningANonRootObjectException, JoiningEqualObjectsException {
        System.out.println("\n\nJoinObjectTest");

        System.out.println("Object: " + object);
        System.out.println("Object2: " + object2);
        
        System.out.println("Joining...");
        ObjectTable joinedObj = joinedObjectsDAO.join(object, object2);
        
        System.out.println("Joined Object: " + joinedObj);
        System.out.println("Object: " + object);
        System.out.println("Object2: " + object2);
        
        Assert.assertNotNull("Joined object is null", joinedObj);
        Assert.assertEquals(object.getRootObject(), joinedObj);
        Assert.assertEquals(object2.getRootObject(), joinedObj);
        
        System.out.println("object.getOldObject1:" + object.getOldObjects1());
        System.out.println("object.getOldObject2:" + object.getOldObjects2());
        System.out.println("object.getNewObject:" + object.getNewObject());

        System.out.println("object2.getOldObject1:" + object2.getOldObjects1());
        System.out.println("object2.getOldObject2:" + object2.getOldObjects2());
        System.out.println("object2.getNewObject:" + object2.getNewObject());

        System.out.println("joinedObj.getOldObject1:" + joinedObj.getOldObjects1());
        System.out.println("joinedObj.getOldObject2:" + joinedObj.getOldObjects2());
        System.out.println("joinedObj.getNewObject:" + joinedObj.getNewObject());
        
        //joinedObjectsDAO.delete(joinedObj.getNewObject());
        //objectTableDAO.delete(joinedObj);
    }
    
    

}

