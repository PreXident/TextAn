/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IGlobalVersionTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.*;
import cz.cuni.mff.ufal.textan.data.test.common.Utils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import org.hibernate.SessionFactory;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, Data.class}, loader = AnnotationConfigContextLoader.class)
public class DataTest {

    @Autowired
    private Data data;

    @Autowired
    private SessionFactory sessionFactory;    
    
    @Autowired
    IObjectTableDAO objectTableDAO;

    @Autowired
    IRelationTypeTableDAO relationTypeTableDAO;
        
    @Autowired
    IDocumentTableDAO documentTableDAO;

    @Autowired
    IGlobalVersionTableDAO globalVersionDAO;

    
    private DocumentTable document;
    private RelationTypeTable relationType;
    private RelationTable withRelation;
    private RelationOccurrenceTable relationOccurrence;
    private ObjectTypeTable objectType;
    private ObjectTable object;
    private AliasTable alias;
    private AliasOccurrenceTable aliasOccurrence;

    
    @Before
    public void setUp() {
        document = new DocumentTable("__[TEST] Example document with some crazy text.");
        relationType = new RelationTypeTable("__[TEST] with");
        withRelation = new RelationTable(relationType);
        relationOccurrence = new RelationOccurrenceTable(withRelation, document, 26, "with");
        objectType = new ObjectTypeTable("__[TEST]objecttype1");
        object = new ObjectTable("__[TEST] letter", objectType);
        alias = new AliasTable(object, "document");
        aliasOccurrence = new AliasOccurrenceTable(17, alias, document);
        
        System.out.println("Setup");
        //System.out.println("If class Method fails, be sure you started the database.");
        assertTrue("You have probably not run the database or the connection is not set properly", data.addRecord(document));
        assertTrue(data.addRecord(relationType));
        assertTrue(data.addRecord(withRelation));
        assertTrue(data.addRecord(relationOccurrence));
        
        assertTrue(data.addRecord(objectType));
        assertTrue(data.addRecord(object));

        
        assertTrue(data.addRecord(alias));
        assertTrue(data.addRecord(aliasOccurrence));
        

        //withRelation.getObjectsInRelation().add(object);

    }
    
    @After
    public void tearDown() {
         System.out.println("\n\nClean");
         /*
         assertTrue(data.deleteRecord(relationOccurrence));
         assertTrue(data.deleteRecord(aliasOccurrence));
         assertTrue(data.deleteRecord(document));
         assertTrue(data.deleteRecord(withRelation));
         assertTrue(data.deleteRecord(withRelation.getRelationType()));
         assertTrue(data.deleteRecord(alias));
         assertTrue(data.deleteRecord(object));
         assertTrue(data.deleteRecord(object.getObjectType()));
         */
         

         
         System.out.println(" clearAllTables");
         Utils.clearTestValues(sessionFactory);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void addAndRemoveInRelationTest() {
        System.out.println("\n\naddAndRemoveInRelationTest");
        InRelationTable user = new InRelationTable(-1, withRelation, object);
        assertTrue("InRelation already exists or cant be added", data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        InRelationTable user2 = null;
        user2 = data.getRecordById(InRelationTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(user2));
    }

    
    @Test
    public void addAndRemoveObjectTypeTest() {
        System.out.println("\n\naddAndRemoveObjectType");
        ObjectTypeTable user = new ObjectTypeTable("__[TEST] Unspecified Object");
        assertTrue("Object type already exists or cant be added", data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        ObjectTypeTable user2 = null;
        user2 = data.getRecordById(ObjectTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveAliasOccurrenceTest() {
        System.out.println("\n\naddAndRemoveAliasOccurrenceTest");
        AliasOccurrenceTable user = new AliasOccurrenceTable(17, alias, document);
        assertTrue("Alias occurence already exists or cant be added", data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        AliasOccurrenceTable user2 = null;
        user2 = data.getRecordById(AliasOccurrenceTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(user));
    }
    
    @Test
    public void InverseMappingObjectTypeToObjectTest() {
       System.out.println("\n\nInverseMappingObjectTypeToObjectTest");
       data.updateRecordById(ObjectTypeTable.class, objectType.getId(), new TableAction<ObjectTypeTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(ObjectTypeTable table) {
                assertNotNull("Object not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of objects: " + Arrays.toString(table.getObjectsOfThisType().toArray()));
                System.out.println("Object: " + object);
                assertTrue("Inverse mapping not working!", table.getObjectsOfThisType().contains(object));
            }
        });
       
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
 
    @Test
    public void InverseMappingRelationToRelationOccurrenceTest() {
       System.out.println("\n\nInverseMappingRelationToRelationOccurrenceTest");
       data.updateRecordById(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(RelationTable table) {
                assertNotNull("Relation not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of relation occurrences: " + Arrays.toString(table.getOccurrences().toArray()));
                System.out.println("Relation Occurrence: " + relationOccurrence);
                assertTrue("Inverse mapping not working!", table.getOccurrences().contains(relationOccurrence));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
    @Test
    public void InverseMappingObjectToAliasTest() {
       System.out.println("\n\nInverseMappingObjectToAliasTest");
       data.updateRecordById(ObjectTable.class, object.getId(), new TableAction<ObjectTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(ObjectTable table) {
                assertNotNull("Object not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Object: " + table);
                System.out.println("Set of aliases: " + Arrays.toString(table.getAliases().toArray()));
                System.out.println("Alias: " + alias);
                //System.out.println("Alias from db: " + data.getRecordById(AliasTable.class, alias.getId()));
                assertTrue("Inverse mapping not working!", table.getAliases().contains(alias));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
    
    @Test
    public void InverseMappingRelationTypeToRelationTest() {
       System.out.println("\n\nInverseMappingRelationTypeToRelationTest");
       data.updateRecordById(RelationTypeTable.class, relationType.getId(), new TableAction<RelationTypeTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(RelationTypeTable table) {
                assertNotNull("Relation not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of relations: " + Arrays.toString(table.getRelationsOfThisType().toArray()));
                System.out.println("Relation: " + withRelation);
                assertTrue("Inverse mapping not working!", table.getRelationsOfThisType().contains(withRelation));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }

    @Test
    public void InverseMappingDocumentToRelationOccurenceTest() {
       System.out.println("\n\nInverseMappingRelationTypeToRelationTest");
       data.updateRecordById(DocumentTable.class, document.getId(), new TableAction<DocumentTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(DocumentTable table) {
                assertNotNull("Document not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of relation occurrences: " + Arrays.toString(table.getRelationOccurrences().toArray()));
                System.out.println("Relation occurrence: " + relationOccurrence);
                assertTrue("Inverse mapping not working!", table.getRelationOccurrences().contains(relationOccurrence));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
    @Test
    public void InverseMappingDocumentToAliasOccurenceTest() {
       System.out.println("\n\nInverseMappingDocumentToAliasOccurenceTest");
       data.updateRecordById(DocumentTable.class, document.getId(), new TableAction<DocumentTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(DocumentTable table) {
                assertNotNull("Document not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of alias occurrences: " + Arrays.toString(table.getAliasOccurrences().toArray()));
                System.out.println("Alias occurrence: " + aliasOccurrence);
                assertTrue("Inverse mapping not working!", table.getAliasOccurrences().contains(aliasOccurrence));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }

    @Test
    public void InverseMappingAliasToAliasOccurenceTest() {
       System.out.println("\n\nInverseMappingAliasToAliasOccurenceTest");
       data.updateRecordById(AliasTable.class, alias.getId(), new TableAction<AliasTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(AliasTable table) {
                assertNotNull("Document not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Set of alias occurrences: " + Arrays.toString(table.getOccurrences().toArray()));
                System.out.println("Alias occurrence: " + aliasOccurrence);
                assertTrue("Inverse mapping not working!", table.getOccurrences().contains(aliasOccurrence));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
    
    @Test(expected = org.hibernate.LazyInitializationException.class)
    public void BadPracticeLazyFetchingTest() {
       System.out.println("\n\nBadPracticeLazyFetchingTest");

       // you cannot use it like this (this relation is not fetched).
       // Look at InverseMappingObjectTypeToObjectTest how to do it properly
       data.getRecordById(ObjectTypeTable.class, objectType.getId()).getObjectsOfThisType().size();

    }
    
    @Test
    public void addAndRemoveRelationTypeTest() {
        System.out.println("\n\naddAndRemoveRelationType");
        RelationTypeTable user = new RelationTypeTable("__[TEST] Unspecified Object");
        assertTrue("Relation type already exists or cant be added", data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        RelationTypeTable user2 = null;
        user2 = data.getRecordById(RelationTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveObjectTest() {
        System.out.println("\n\naddAndRemoveObject");
        ObjectTypeTable ott = new ObjectTypeTable("__[TEST] ObjectType1");
        assertTrue("Object type already exists or cant be added", data.addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            ObjectTable ot = data.getRecordById(ObjectTable.class, 1L);
            System.out.println("ot = " + ot);
            
            try {
                ot = new ObjectTable("__[TEST] object data XXX ###asd", ott);
                assertTrue("Object type already exists or cant be added: " + ot, data.addRecord(ot));
                System.out.println("Object added: " + ot);
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Object cant be deleted: " + ot, data.deleteRecord(ot));
                System.out.println("Object deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("data.deleteRecord(ott)", data.deleteRecord(ott));
            System.out.println("Object type deleted: " + ott);

        }
    }
    
    @Test
    public void addAndRemoveRelationTest() {
        System.out.println("\n\naddAndRemoveRelation");
        RelationTypeTable relationType = new RelationTypeTable("__[TEST] RelationType1");
        assertTrue("Object type already exists or cant be added", data.addRecord(relationType));
        System.out.println("Object typed added: " + relationType);

            RelationTable relation = data.getRecordById(RelationTable.class, 1L);
            System.out.println("ot = " + relation);

                relation = new RelationTable(relationType);
                assertTrue("Relation type already exists or cant be added: " + relation, data.addRecord(relation));
                System.out.println("Relation added: " + relation);
                
                //assertTrue("Relation cant be deleted: " + relation, data.deleteRecord(relation));
                //System.out.println("Relation deleted: " + relation);
            
            

            assertTrue("data.deleteRecord(relationType)", data.deleteRecord(relationType));
            System.out.println("Relation type deleted: " + relationType);


    }
   
    @Test
    public void addAndRemoveDocumentTest() {
        System.out.println("\n\naddAndRemoveDocument");
        DocumentTable document = new DocumentTable("__[TEST] Extra long text from report");
        assertTrue("Document already exists or cant be added", data.addRecord(document));
        long id = document.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        DocumentTable doc2 = null;
        doc2 = data.getRecordById(DocumentTable.class, id);
        assertTrue("doc2.equals(doc): doc = " + document + "; doc2 = " + doc2, doc2.equals(document));
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(doc2));
    }

    @Test
    public void addAndRemoveAliasTest() {
        System.out.println("\n\naddAndRemoveAlias");
        AliasTable ott = new AliasTable(object, "example");
        assertTrue("Alias already exists or cant be added", data.addRecord(ott));
        System.out.println("Alias added: " + ott);
        try {
            assertTrue("data.deleteRecord(ott)", data.deleteRecord(ott));
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("Object type deleted: " + ott);

        }
    }
    
    /*
    @Test
    public void deleteTestValuesTest() {
         System.out.println("deleteTestValuesTest");

         System.out.println("relation types: " + relationTypeTableDAO.findAll().size());
         System.out.println("documents: " + documentTableDAO.findAll().size());
         System.out.println("objects: " + objectTableDAO.findAll().size());

         System.out.println("deleting...");
         Utils.deleteTestValues(sessionFactory);

         System.out.println("relation types: " + relationTypeTableDAO.findAll().size());
         System.out.println("documents: " + documentTableDAO.findAll().size());
         System.out.println("objects: " + objectTableDAO.findAll().size());

         
         Assert.assertEquals("There is an test Document left", 0, documentTableDAO.findAllDocumentsByFullText("[TEST]").size());
         
         //Assert.assertEquals("There is an test Relation Type left", 0, relationTypeTableDAO.("[TEST]").size());
         
         Assert.assertEquals("There is an test Object left", 0, objectTableDAO.findAllByAliasSubstring("[TEST]").size());
    }
    */

    @Test
    public void addAndRemoveJoinedObjectsTest() {
        System.out.println("\n\naddAndRemoveJoinedObjectsTest");
        JoinedObjectsTable user = new JoinedObjectsTable(object, object, object);
        System.out.println("user = " + user);
        assertTrue("Object type already exists or cant be added", data.addRecord(user));
        long id = user.getId();
        System.out.println("user.id = " + id);
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);

        user = data.getRecordById(JoinedObjectsTable.class, id);
        JoinedObjectsTable user2 = data.getRecordById(JoinedObjectsTable.class, id);
        
        assertEquals("user2.equals(user):\nuser1 = " + user + ";\nuser2 = " + user2, user, user2);
        assertTrue("data.deleteRecord(user2)", data.deleteRecord(user2));
    }
    
    
    // TODO:IsInRelationBidirectionalTest
    
    
    /*
    @Test
    public void IsInRelationBidirectionalTest() {
        System.out.println("\n\nInverseMappingIsInRelationTest");
        System.out.println("Objects: " + Arrays.toString(withRelation.getObjectsInRelation().toArray()));
        DataSingleton.getSingleton().performActionOnRecord(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {

            @Override
            public void action(RelationTable table) {
                table.getObjectsInRelation().add(object);
            }
        });
        // withRelation object is not actual... you have to load it again:
        DataSingleton.getSingleton().performActionOnRecord(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {
            @Override
            public void action(RelationTable table) {
                System.out.println("Objects: " + Arrays.toString(table.getObjectsInRelation().toArray()));
                assertTrue("Object is not present in list of objects", table.getObjectsInRelation().contains(object));
            }
        });
        DataSingleton.getSingleton().performActionOnRecord(ObjectTable.class, object.getId(), new TableAction<ObjectTable>() {

            @Override
            public void action(ObjectTable table) {
                System.out.println("Relations: " + Arrays.toString(table.getRelations().toArray()));
                assertTrue(table.getRelations().contains(withRelation));
            }
        });
        DataSingleton.getSingleton().performActionOnRecord(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {

            @Override
            public void action(RelationTable table) {
                table.getObjectsInRelation().clear();
            }
        });
        DataSingleton.getSingleton().performActionOnRecord(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {
            @Override
            public void action(RelationTable table) {
                System.out.println("Objects: " + Arrays.toString(table.getObjectsInRelation().toArray()));
                assertTrue("Object list is not empty", table.getObjectsInRelation().isEmpty());
            }
        });
        DataSingleton.getSingleton().performActionOnRecord(ObjectTable.class, object.getId(), new TableAction<ObjectTable>() {

            @Override
            public void action(ObjectTable table) {
                System.out.println("Relations: " + Arrays.toString(table.getRelations().toArray()));
                assertTrue("Relations list is not empty", table.getRelations().isEmpty());
            }
        });
        
    }
    */
    /*
    @Test
    public void alwaysFail() {
        assertTrue(false);
    }*/
}
