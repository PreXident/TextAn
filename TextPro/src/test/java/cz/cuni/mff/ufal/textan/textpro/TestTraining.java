/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.textpro;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasOccurrenceTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationOccurrenceTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IRelationTypeTableDAO;
import cz.cuni.mff.ufal.textan.textpro.configs.TextProConfig;
import cz.cuni.mff.ufal.textan.textpro.learning.TrainWeka;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import weka.core.Attribute;
import weka.core.Instance;

/**
 * This testing package is devoted to Weka Features
 * @author HOANGT
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TextProConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TestTraining {

    @Autowired
    IAliasOccurrenceTableDAO aliasOccurrenceTableDAO;

    @Autowired
    IAliasTableDAO aliasTableDAO;

    @Autowired
    IJoinedObjectsTableDAO joinedObjectsTableDAO;

    @Autowired
    IObjectTableDAO objectTableDAO;

    @Autowired
    IObjectTypeTableDAO objectTypeTableDAO;

    @Autowired
    IRelationOccurrenceTableDAO relationOccurrenceTableDAO;

    @Autowired
    IRelationTableDAO relationTableDAO;

    @Autowired
    IRelationTypeTableDAO typeTableDAO;

    @Autowired
    IDocumentTableDAO documentTableDAO;
    
    @Autowired
    ITextPro textPro;

    @Test
    public void TestRank() {
        TrainWeka train = new TrainWeka();
        Instance thisInstance = new Instance(5);
        thisInstance.setValue((Attribute)train.fvWekaAttributes.elementAt(0), 1.0);
        thisInstance.setValue((Attribute)train.fvWekaAttributes.elementAt(1), 1.0);
        thisInstance.setValue((Attribute)train.fvWekaAttributes.elementAt(2), 1.0);
        thisInstance.setValue((Attribute)train.fvWekaAttributes.elementAt(3), 1.0);
        thisInstance.setValue((Attribute)train.fvWekaAttributes.elementAt(4), "positive");
        //TODO: TEST
    }
}
