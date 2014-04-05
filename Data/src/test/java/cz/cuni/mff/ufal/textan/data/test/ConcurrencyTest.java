/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.repositories.TableAction;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
public class ConcurrencyTest {

    @Autowired
    Data data;
    
    @Before
    public void setUp() {
        System.out.println("Setup:");
    }
    
    @After
    public void tearDown() {
        System.out.println("\n\nTear down");
    }

    
    @Test
    public void ConcurrencyRewriteJustRewrittenTest() {
        System.out.println("\n\nConcurrencyRewriteJustRewrittenTest");
        ObjectTypeTable objectType1 = new ObjectTypeTable("__[TEST]objecttype 1");
        data.addRecord(objectType1);
        final long id = objectType1.getId();
        data.updateRecordById(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

            @Override
            public void action(ObjectTypeTable table) {
                table.setName("__[TEST]objecttype 1 changed");
                data.updateRecordById(ObjectTypeTable.class, id, new TableAction<ObjectTypeTable>() {

                    @Override
                    public void action(ObjectTypeTable table) {
                        table.setName("__[TEST]objecttype 1 changed snd time");
                        System.out.println("commiting __[TEST]objecttype 1 changed snd time");
                    }
                });
                System.out.println("sommiting __[TEST]objecttype 1 changed");
            }
        });
        ObjectTypeTable objectType2 = data.getRecordById(ObjectTypeTable.class, objectType1.getId());
        System.out.println("Changed object: " + objectType2);
        data.deleteRecord(objectType1);
    }
    
    // TODO: Concurrency throws exception

}
