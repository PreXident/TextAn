/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.repositories.TableAction;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import java.util.Arrays;
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
public class DataBatchTest {
    
    static final int SMALL_BATCH_SIZE = 10;
    static final String OBJECT_TYPE_PREFIX = "[TEST] Object Type ";
    static ObjectTypeTable[] objectTypes = new ObjectTypeTable[SMALL_BATCH_SIZE];
    int count;
    
    
    public DataBatchTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            objectTypes[i] = new ObjectTypeTable(OBJECT_TYPE_PREFIX + i);
            DataSingleton.getSingleton().addRecord(objectTypes[i]);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            DataSingleton.getSingleton().deleteRecord(objectTypes[i]);
        }
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
    public void selectAllTest() {
        count = 0;
        DataSingleton.getSingleton().selectAll(ObjectTypeTable.class, new TableAction<ObjectTypeTable>() {

            @Override
            public void action(ObjectTypeTable table) {
                if (Arrays.asList(objectTypes).contains(table))
                  count++;
            }
        });
        System.out.println("Count = " + count);
        assertTrue("Did not found all the values", count >= SMALL_BATCH_SIZE);

    }
}
