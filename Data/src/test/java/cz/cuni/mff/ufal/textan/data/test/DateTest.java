/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.dao.DocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.StaleObjectStateException;
import org.junit.After;
import org.junit.Assert;
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
@ContextConfiguration(classes = {DataConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DateTest {

    @Autowired
    IDocumentTableDAO documentDAO;
    
    private DocumentTable objectType1;
    private DocumentTable objectType2;
    
    @Before
    public void setUp() {
        System.out.println("Setup:");
        objectType1 = new DocumentTable("__[TEST]objecttype 1");
        objectType2 = new DocumentTable("__[TEST]objecttype 2");
        objectType2.getAddedDate().setTime(objectType2.getAddedDate().getTime()+2000);
        
        documentDAO.add(objectType1);
        documentDAO.add(objectType2);
    }
    
    @After
    public void tearDown() {
        System.out.println("\n\nTear down");
        documentDAO.delete(objectType1);
        documentDAO.delete(objectType2);
    }

    
    @Test
    public void DocumentDatePrecisionTest() {
        System.out.println("\n\nConcurrencyRewriteJustRewrittenTest");
        System.out.println("Document1: " + (objectType1 = documentDAO.find(objectType1.getId())));
        System.out.println("Document2: " + (objectType2 = documentDAO.find(objectType2.getId())));
        Assert.assertNotEquals(objectType1.getAddedDate(), objectType2.getAddedDate());
    }
    
    // TODO: Concurrency throws exception

}
