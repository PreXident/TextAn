/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
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
    
    public DataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
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
    public void addAndRemoveObjectType() {
        ObjectTypeTable user = new ObjectTypeTable("__Unspecified Object");
        assertTrue("Object already exists or cant be added", Data.addRecord(user));
        long id = user.getId();
        assertTrue("id > 0", id > 0);
        System.out.println("id: " + id);
        ObjectTypeTable user2 = null;
        user2 = (ObjectTypeTable)Data.getRecordById(ObjectTypeTable.class, id);
        assertTrue("user2.equals(user): user = " + user + "; user2 = " + user2, user2.equals(user));
        assertTrue("Data.deleteRecord(user2)", Data.deleteRecord(user2));
    }
}
