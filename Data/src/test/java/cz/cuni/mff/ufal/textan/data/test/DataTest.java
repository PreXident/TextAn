/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.TableAction;
import cz.cuni.mff.ufal.textan.data.tables.*;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Venda
 */
public class DataTest {
    
    private static DocumentTable document;
    private static RelationTypeTable relationType;
    private static RelationTable withRelation;
    private static RelationOccurrenceTable relationOccurrence;
    private static ObjectTypeTable objectType;
    private static ObjectTable object;
    private static AliasTable alias;
    private static AliasOccurrenceTable aliasOccurrence;
    
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
        assertTrue("You have probably not run the database or the connection is not set properly", DataSingleton.getSingleton().addRecord(document));
        assertTrue(DataSingleton.getSingleton().addRecord(withRelation));
        assertTrue(DataSingleton.getSingleton().addRecord(relationOccurrence));
        assertTrue(DataSingleton.getSingleton().addRecord(object));
        assertTrue(DataSingleton.getSingleton().addRecord(alias)); 
        assertTrue(DataSingleton.getSingleton().addRecord(aliasOccurrence)); 
        //withRelation.getObjectsInRelation().add(object);

    }
    
    @AfterClass
    public static void tearDownClass() {
         System.out.println("\n\nClean");
         assertTrue(DataSingleton.getSingleton().deleteRecord(relationOccurrence));
         assertTrue(DataSingleton.getSingleton().deleteRecord(aliasOccurrence));
         assertTrue(DataSingleton.getSingleton().deleteRecord(document));
         assertTrue(DataSingleton.getSingleton().deleteRecord(withRelation));
         assertTrue(DataSingleton.getSingleton().deleteRecord(withRelation.getRelationType()));
         assertTrue(DataSingleton.getSingleton().deleteRecord(alias));
         assertTrue(DataSingleton.getSingleton().deleteRecord(object));
         assertTrue(DataSingleton.getSingleton().deleteRecord(object.getObjectType()));
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
    // @Test
    // public void hello() {}

    @Test
    public void addAndRemoveInRelationTest() {
        System.out.println("\n\naddAndRemoveInRelationTest");
        InRelationTable user = new InRelationTable(-1, withRelation, object);
        assertTrue("InRelation already exists or cant be added", DataSingleton.getSingleton().addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        InRelationTable user2 = null;
        user2 = DataSingleton.getSingleton().getRecordById(InRelationTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(user2));
    }

    
    @Test
    public void addAndRemoveObjectTypeTest() {
        System.out.println("\n\naddAndRemoveObjectType");
        ObjectTypeTable user = new ObjectTypeTable("__Unspecified Object");
        assertTrue("Object type already exists or cant be added", DataSingleton.getSingleton().addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        ObjectTypeTable user2 = null;
        user2 = DataSingleton.getSingleton().getRecordById(ObjectTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveAliasOccurrenceTest() {
        System.out.println("\n\naddAndRemoveAliasOccurrenceTest");
        AliasOccurrenceTable user = new AliasOccurrenceTable(17, alias, document);
        assertTrue("Alias occurence already exists or cant be added", DataSingleton.getSingleton().addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        AliasOccurrenceTable user2 = null;
        user2 = DataSingleton.getSingleton().getRecordById(AliasOccurrenceTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(user2));
    }
    
    @Test
    public void InverseMappingObjectTypeToObjectTest() {
       System.out.println("\n\nInverseMappingObjectTypeToObjectTest");
       DataSingleton.getSingleton().performActionOnRecord(ObjectTypeTable.class, objectType.getId(), new TableAction<ObjectTypeTable>() {
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
       DataSingleton.getSingleton().performActionOnRecord(RelationTable.class, withRelation.getId(), new TableAction<RelationTable>() {
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
       DataSingleton.getSingleton().performActionOnRecord(ObjectTable.class, object.getId(), new TableAction<ObjectTable>() {
            // LAMBDA EXP COULD BE POSSIBLE AS WELL
            @Override
            public void action(ObjectTable table) {
                assertNotNull("Object not found (Deleted in some other test and that shouldnt be done) Bitch please", table);
                System.out.println("Object: " + table);
                System.out.println("Set of aliases: " + Arrays.toString(table.getAliases().toArray()));
                System.out.println("Alias: " + alias);
                //System.out.println("Alias from db: " + DataSingleton.getSingleton().getRecordById(AliasTable.class, alias.getId()));
                assertTrue("Inverse mapping not working!", table.getAliases().contains(alias));
            }
        });
       // you must use performActionOnRecord here. Otherwise (getRecordById) would throw an exception (there cant be used lazy fetching)
    }
    
    @Test
    public void InverseMappingRelationTypeToRelationTest() {
       System.out.println("\n\nInverseMappingRelationTypeToRelationTest");
       DataSingleton.getSingleton().performActionOnRecord(RelationTypeTable.class, relationType.getId(), new TableAction<RelationTypeTable>() {
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
       DataSingleton.getSingleton().performActionOnRecord(DocumentTable.class, document.getId(), new TableAction<DocumentTable>() {
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
       DataSingleton.getSingleton().performActionOnRecord(DocumentTable.class, document.getId(), new TableAction<DocumentTable>() {
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
       DataSingleton.getSingleton().performActionOnRecord(AliasTable.class, alias.getId(), new TableAction<AliasTable>() {
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
       DataSingleton.getSingleton().getRecordById(ObjectTypeTable.class, objectType.getId()).getObjectsOfThisType().size();

    }
    
    @Test
    public void addAndRemoveRelationTypeTest() {
        System.out.println("\n\naddAndRemoveRelationType");
        RelationTypeTable user = new RelationTypeTable("__Unspecified Object");
        assertTrue("Relation type already exists or cant be added", DataSingleton.getSingleton().addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        RelationTypeTable user2 = null;
        user2 = DataSingleton.getSingleton().getRecordById(RelationTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveObjectTest() {
        System.out.println("\n\naddAndRemoveObject");
        ObjectTypeTable ott = new ObjectTypeTable("__ObjectType1");
        assertTrue("Object type already exists or cant be added", DataSingleton.getSingleton().addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            ObjectTable ot = DataSingleton.getSingleton().getRecordById(ObjectTable.class, 1L);
            System.out.println("ot = " + ot);
            
            try {
                ot = new ObjectTable("__object data XXX ###asd", ott);
                assertTrue("Object type already exists or cant be added: " + ot, DataSingleton.getSingleton().addRecord(ot));
                System.out.println("Object added: " + ot);
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Object cant be deleted: " + ot, DataSingleton.getSingleton().deleteRecord(ot));
                System.out.println("Object deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("DataSingleton.getSingleton().deleteRecord(ott)", DataSingleton.getSingleton().deleteRecord(ott));
            System.out.println("Object type deleted: " + ott);

        }
    }
    
    @Test
    public void addAndRemoveRelationTest() {
        System.out.println("\n\naddAndRemoveRelation");
        RelationTypeTable ott = new RelationTypeTable("__RelationType1");
        assertTrue("Object type already exists or cant be added", DataSingleton.getSingleton().addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            RelationTable ot = DataSingleton.getSingleton().getRecordById(RelationTable.class, 1L);
            System.out.println("ot = " + ot);
            
            try {
                ot = new RelationTable(ott);
                assertTrue("Relation type already exists or cant be added: " + ot, DataSingleton.getSingleton().addRecord(ot));
                System.out.println("Relation added: " + ot);
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Relation cant be deleted: " + ot, DataSingleton.getSingleton().deleteRecord(ot));
                System.out.println("Relation deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("DataSingleton.getSingleton().deleteRecord(ott)", DataSingleton.getSingleton().deleteRecord(ott));
            System.out.println("Relation type deleted: " + ott);

        }
    }
   
    @Test
    public void addAndRemoveDocumentTest() {
        System.out.println("\n\naddAndRemoveDocument");
        DocumentTable document = new DocumentTable("__Extra long text from report");
        assertTrue("Document already exists or cant be added", DataSingleton.getSingleton().addRecord(document));
        long id = document.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        DocumentTable doc2 = null;
        doc2 = DataSingleton.getSingleton().getRecordById(DocumentTable.class, id);
        assertTrue("doc2.equals(doc): doc = " + document + "; doc2 = " + doc2, doc2.equals(document));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(doc2));
    }

    @Test
    public void addAndRemoveAliasTest() {
        System.out.println("\n\naddAndRemoveAlias");
        AliasTable ott = new AliasTable(object, "example");
        assertTrue("Alias already exists or cant be added", DataSingleton.getSingleton().addRecord(ott));
        System.out.println("Alias added: " + ott);
        try {
            assertTrue("DataSingleton.getSingleton().deleteRecord(ott)", DataSingleton.getSingleton().deleteRecord(ott));
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("Object type deleted: " + ott);

        }
    }

    @Test
    public void addAndRemoveJoinedObjectsTest() {
        System.out.println("\n\naddAndRemoveJoinedObjectsTest");
        JoinedObjectsTable user = new JoinedObjectsTable(object, object, object);
        assertTrue("Object type already exists or cant be added", DataSingleton.getSingleton().addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        JoinedObjectsTable user2 = null;
        user2 = DataSingleton.getSingleton().getRecordById(JoinedObjectsTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("DataSingleton.getSingleton().deleteRecord(user2)", DataSingleton.getSingleton().deleteRecord(user2));
    }
    
    @Test
    public void ConcurrencyRewriteJustRewrittenTest() {
        ObjectTypeTable objectType1 = new ObjectTypeTable("__[TEST]objecttype 1");
        DataSingleton.getSingleton().addRecord(objectType1);
        final long id = objectType1.getId();
        DataSingleton.getSingleton().performActionOnRecord(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

            @Override
            public void action(ObjectTypeTable table) {
                table.setName("__[TEST]objecttype 1 changed");
                DataSingleton.getSingleton().performActionOnRecord(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

                    @Override
                    public void action(ObjectTypeTable table) {
                        table.setName("__[TEST]objecttype 1 changed snd time");
                    }
                });
            }
        });
    }
    
    // TODO: Concurrency throws exception
    
    
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
