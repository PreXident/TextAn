/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.TableAction;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
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
public class ConcurrencyTest {
    
    public ConcurrencyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Setup:");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("\n\nTear down");
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void ConcurrencyRewriteJustRewrittenTest() {
        System.out.println("\n\nConcurrencyRewriteJustRewrittenTest");
        ObjectTypeTable objectType1 = new ObjectTypeTable("__[TEST]objecttype 1");
        DataSingleton.getSingleton().addRecord(objectType1);
        final long id = objectType1.getId();
        DataSingleton.getSingleton().updateRecordById(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

            @Override
            public void action(ObjectTypeTable table) {
                table.setName("__[TEST]objecttype 1 changed");
                DataSingleton.getSingleton().updateRecordById(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

                    @Override
                    public void action(ObjectTypeTable table) {
                        table.setName("__[TEST]objecttype 1 changed snd time");
                        System.out.println("commiting __[TEST]objecttype 1 changed snd time");
                    }
                });
                System.out.println("sommiting __[TEST]objecttype 1 changed");
            }
        });
        ObjectTypeTable objectType2 = DataSingleton.getSingleton().getRecordById(ObjectTypeTable.class, objectType1.getId());
        System.out.println("Changed object: " + objectType2);
        DataSingleton.getSingleton().deleteRecord(objectType1);
    }
    
    // TODO: Concurrency throws exception

}
