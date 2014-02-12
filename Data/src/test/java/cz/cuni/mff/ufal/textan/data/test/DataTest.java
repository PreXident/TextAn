/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.repositories.TableAction;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
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
    
    private static DocumentTable document = new DocumentTable("__[TEST] Example document with some crazy text.");
    private static RelationTable withRelation = new RelationTable(new RelationTypeTable("__[TEST] with"));
    private static ObjectTable object = new ObjectTable("__[TEST] letter", new ObjectTypeTable("__[TEST]objecttype1"));
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Setup");
        //System.out.println("If class Method fails, be sure you started the database.");
        assertTrue("You have probably not run the database or the connection is not set properly", Data.addRecord(document));
        Data.addRecord(withRelation);
        Data.addRecord(object);         
    }
    
    @AfterClass
    public static void tearDownClass() {
         System.out.println("\n\nClean");
         assertTrue(Data.deleteRecord(document));
         assertTrue(Data.deleteRecord(withRelation));
         assertTrue(Data.deleteRecord(withRelation.getRelationType()));
         assertTrue(Data.deleteRecord(object));
         assertTrue(Data.deleteRecord(object.getObjectType()));
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
    public void addAndRemoveObjectTypeTest() {
        System.out.println("\n\naddAndRemoveObjectType");
        ObjectTypeTable user = new ObjectTypeTable("__Unspecified Object");
        assertTrue("Object type already exists or cant be added", Data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        ObjectTypeTable user2 = null;
        user2 = Data.getRecordById(ObjectTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("Data.deleteRecord(user2)", Data.deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveRelationTypeTest() {
        System.out.println("\n\naddAndRemoveRelationType");
        RelationTypeTable user = new RelationTypeTable("__Unspecified Object");
        assertTrue("Relation type already exists or cant be added", Data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        RelationTypeTable user2 = null;
        user2 = Data.getRecordById(RelationTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("Data.deleteRecord(user2)", Data.deleteRecord(user2));
    }
    
    @Test
    public void addAndRemoveObjectTest() {
        System.out.println("\n\naddAndRemoveObject");
        ObjectTypeTable ott = new ObjectTypeTable("__ObjectType1");
        assertTrue("Object type already exists or cant be added", Data.addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            ObjectTable ot = Data.getRecordById(ObjectTable.class, 1L);
            System.out.println("ot = " + ot);
            
            try {
                ot = new ObjectTable("__object data XXX ###asd", ott);
                assertTrue("Object type already exists or cant be added: " + ot, Data.addRecord(ot));
                System.out.println("Object added: " + ot);
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Object cant be deleted: " + ot, Data.deleteRecord(ot));
                System.out.println("Object deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("Data.deleteRecord(ott)", Data.deleteRecord(ott));
            System.out.println("Object type deleted: " + ott);

        }
    }
    
    @Test
    public void addAndRemoveRelationTest() {
        System.out.println("\n\naddAndRemoveRelation");
        RelationTypeTable ott = new RelationTypeTable("__RelationType1");
        assertTrue("Object type already exists or cant be added", Data.addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            RelationTable ot = Data.getRecordById(RelationTable.class, 1L);
            System.out.println("ot = " + ot);
            
            try {
                ot = new RelationTable(ott);
                assertTrue("Relation type already exists or cant be added: " + ot, Data.addRecord(ot));
                System.out.println("Relation added: " + ot);
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Relation cant be deleted: " + ot, Data.deleteRecord(ot));
                System.out.println("Relation deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("Data.deleteRecord(ott)", Data.deleteRecord(ott));
            System.out.println("Relation type deleted: " + ott);

        }
    }
  
    
    @Test
    public void isInRelationRelationTest() {
        System.out.println("\n\nisInRelationRelationTest");
        RelationTypeTable ott = new RelationTypeTable("__RelationType1");
        assertTrue("Object type already exists or cant be added", Data.addRecord(ott));
        System.out.println("Object typed added: " + ott);
        try {
            // TODO OBJECT ADD AND REMOVE
            
            RelationTable ot = Data.getRecordById(RelationTable.class, 3L);
            System.out.println("ot = " + ot);
            
            // This line cannot be uncomented - throws an exception. Use performActionOnRecord instead
            //System.out.println("Objects in relation: " + Arrays.toString(ot.getObjectsInRelation().toArray()));
            
            Data.performActionOnRecord(RelationTable.class, 3L, new TableAction<RelationTable>() {

                @Override
                public void action(RelationTable table) {
                    System.out.println("table = " + table);
                    System.out.println("Objects in relation table: " + Arrays.toString(table.getObjectsInRelation().toArray()));
                }
            });
            
            try {
                ot = new RelationTable(ott);
                assertTrue("Relation type already exists or cant be added: " + ot, Data.addRecord(ot));
                System.out.println("Relation added: " + ot);
                
                
            } catch (Exception e) {
                throw e;
            } finally {
                assertTrue("Relation cant be deleted: " + ot, Data.deleteRecord(ot));
                System.out.println("Relation deleted: " + ot);
            }
            
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            assertTrue("Data.deleteRecord(ott)", Data.deleteRecord(ott));
            System.out.println("Relation type deleted: " + ott);

        }
    }

    @Test
    public void addAndRemoveDocumentTest() {
        System.out.println("\n\naddAndRemoveDocument");
        DocumentTable document = new DocumentTable("__Extra long text from report");
        assertTrue("Document already exists or cant be added", Data.addRecord(document));
        long id = document.getId();
        assertTrue("id > 0", id > 0);
        //System.out.println("id: " + id);
        DocumentTable doc2 = null;
        doc2 = Data.getRecordById(DocumentTable.class, id);
        assertTrue("doc2.equals(doc): doc = " + document + "; doc2 = " + doc2, doc2.equals(document));
        assertTrue("Data.deleteRecord(user2)", Data.deleteRecord(doc2));
    }

    @Test
    public void addAndRemoveAliasTest() {
        System.out.println("\n\naddAndRemoveAlias");
        AliasTable ott = new AliasTable(object, "example");
        assertTrue("Alias already exists or cant be added", Data.addRecord(ott));
        System.out.println("Alias added: " + ott);
        try {
            assertTrue("Data.deleteRecord(ott)", Data.deleteRecord(ott));
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("Object type deleted: " + ott);

        }
    }

    
    /*
    @Test
    public void alwaysFail() {
        assertTrue(false);
    }*/
}
