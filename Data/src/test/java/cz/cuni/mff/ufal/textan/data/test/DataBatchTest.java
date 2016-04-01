/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.test.common.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * @author Vaclav Pernicka
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, Data.class}, loader = AnnotationConfigContextLoader.class)
public class DataBatchTest {

    static final int SMALL_BATCH_SIZE = 10;
    static final String OBJECT_TYPE_PREFIX = "[TEST] Object Type ";
    final ObjectTypeTable[] objectTypes = new ObjectTypeTable[SMALL_BATCH_SIZE];
    int count;
    @Autowired
    private Data data;

    @Before
    public void setUp() {
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            objectTypes[i] = new ObjectTypeTable(OBJECT_TYPE_PREFIX + i);
            data.addRecord(objectTypes[i]);
        }
    }

    @After
    public void tearDown() {
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            data.deleteRecord(objectTypes[i]);
        }
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void selectAllTest() {
        count = 0;
        data.selectAll(ObjectTypeTable.class, table -> {
            if (Arrays.asList(objectTypes).contains(table))
                count++;
        });
        System.out.println("Count = " + count);
        assertTrue("Did not found all the values", count >= SMALL_BATCH_SIZE);

    }
}
