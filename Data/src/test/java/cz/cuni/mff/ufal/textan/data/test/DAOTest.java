/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.configs.DataConfig;
import cz.cuni.mff.ufal.textan.data.repositories.Data;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AliasOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

/**
 *
 * @author Václav Pernička
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DAOTest {
    @Autowired
    Data data;

    @Autowired
    IObjectTableDAO objectTableDAO;
    
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
        assertTrue(data.addRecord(withRelation));
        assertTrue(data.addRecord(relationOccurrence));
        assertTrue(data.addRecord(object));
        assertTrue(data.addRecord(alias));
        assertTrue(data.addRecord(aliasOccurrence));
        //withRelation.getObjectsInRelation().add(object);

    }
    
    @After
    public void tearDown() {
         System.out.println("\n\nClean");
         assertTrue(data.deleteRecord(relationOccurrence));
         assertTrue(data.deleteRecord(aliasOccurrence));
         assertTrue(data.deleteRecord(document));
         assertTrue(data.deleteRecord(withRelation));
         assertTrue(data.deleteRecord(withRelation.getRelationType()));
         assertTrue(data.deleteRecord(alias));
         assertTrue(data.deleteRecord(object));
         assertTrue(data.deleteRecord(object.getObjectType()));
    }
    
    
    @Test
    public void findAllTest() {
        List<ObjectTable> res = objectTableDAO.findAll();
        for (ObjectTable objectTable : res) {
            if (res.equals(objectTable))
                return;
        }
        assertTrue("Object not found", false);
    }
}
