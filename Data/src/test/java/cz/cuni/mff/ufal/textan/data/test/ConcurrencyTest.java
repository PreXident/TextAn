/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.test.common.TableAction;
import cz.cuni.mff.ufal.textan.data.test.common.Data;
import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import org.hibernate.StaleObjectStateException;
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
 * @author Vaclav Pernicka
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, Data.class}, loader = AnnotationConfigContextLoader.class)
public class ConcurrencyTest {

    @Autowired
    Data data;
    private DocumentTable objectType1;
    
    @Before
    public void setUp() {
        System.out.println("Setup:");
        objectType1 = new DocumentTable("__[TEST]objecttype 1");
        data.addRecord(objectType1);

    }
    
    @After
    public void tearDown() {
        System.out.println("\n\nTear down");
        data.deleteRecord(objectType1);
    }

    
    @Test(
            //expected = StaleObjectStateException.class
    )
    public void DocumentRewriteJustRewrittenTest() {
        System.out.println("\n\nConcurrencyRewriteJustRewrittenTest");
        final long id = objectType1.getId();
        data.updateRecordById(DocumentTable.class, id, new TableAction<DocumentTable>() {

            @Override
            public void action(DocumentTable table) {
                table.setText("__[TEST]objecttype 1 changed");
                data.updateRecordById(DocumentTable.class, id, new TableAction<DocumentTable>() {

                    @Override
                    public void action(DocumentTable table) {
                        table.setText("__[TEST]objecttype 1 changed snd time");
                        System.out.println("commiting __[TEST]objecttype 1 changed snd time");
                    }
                });
                System.out.println("sommiting __[TEST]objecttype 1 changed");
            }
        });
        DocumentTable objectType2 = data.getRecordById(DocumentTable.class, objectType1.getId());
        System.out.println("Changed object: " + objectType2);
    }
    
    // TODO: Concurrency throws exception

}
