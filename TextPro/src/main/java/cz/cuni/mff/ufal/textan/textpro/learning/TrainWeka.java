/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro.learning;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.FeaturesComputeValue;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author HOANGT
 */
public class TrainWeka {
    
    FastVector fvWekaAttributes;
    public Instances isTrainingSet;
    /*
     * Default constructor
     */
    public TrainWeka() {

        // Attribute = Features
        Attribute StringSimilarityMaximum = new Attribute("StringSimilarityMaximum");
        Attribute StringSimilarityMinimum = new Attribute("StringSimilarityMinimum");
        Attribute StringSimilarityAverage = new Attribute("StringSimilarityAverage");
        Attribute TypeComparison = new Attribute("TypeComparison");

        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(2);
        fvClassVal.addElement("positive"); // True
        fvClassVal.addElement("negative"); // False
        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

        
        // Declare the feature vector
        fvWekaAttributes = new FastVector(3);
        fvWekaAttributes.addElement(StringSimilarityMaximum);
        fvWekaAttributes.addElement(TypeComparison);
        fvWekaAttributes.addElement(ClassAttribute);
    }
    
    /*
    * Function doTraining, the same function as previous version, but different classifier
    */
    public Classifier doTraining(IObjectTableDAO objectTableDAO, IAliasTableDAO aliasTableDAO) {
        isTrainingSet = new Instances("Rel", this.fvWekaAttributes, 10);
        // Create the list of all objects
        List<ObjectTable> objectTable = objectTableDAO.findAll();
        
        // Create YES
        for (ObjectTable obt : objectTable) {

            long type = obt.getObjectType().getId(); 
            Set<AliasTable> als = obt.getAliases();
            
            String first_alias_text = als.iterator().next().getAlias(); //first_alias.getAlias();
            
            // Create a fake entity for training yes
            Entity e = new Entity(first_alias_text, 0, 0, type);
            // Create another fake entity for training no
            Entity e_reverse = new Entity(new StringBuilder(first_alias_text).reverse().toString(), 0, 0, type - 1);
            // create an instance from this pair
            Instance ins = CreateInstanceBasic(e, obt, aliasTableDAO, objectTableDAO, "positive");
            Instance ins_reverse = CreateInstanceBasic(e_reverse, obt, aliasTableDAO, objectTableDAO, "negative");
            
            
            // Add the instance to dataset
            isTrainingSet.add(ins);
            isTrainingSet.add(ins_reverse);
        }
        
        // Create a model
        Classifier cModel = (Classifier)new IBk();
        try {
            cModel.buildClassifier(isTrainingSet);
        } catch (Exception ex) {
            Logger.getLogger(TrainWeka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cModel;
    }
    
    public Instance CreateInstanceBasic
        (Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO, 
                IObjectTableDAO objectTableDAO, String target) {
        
        // Instance    
        Instance thisInstance = new Instance(4);
        
        // Compute value of instance
        FeaturesComputeValue fcv = new FeaturesComputeValue();
        // Get all alias
        List<AliasTable> aliasTable = aliasTableDAO.findAllAliasesOfObject(obj);

        // Feature 1: The similarity between entity text and object alias
        double highestSim = 0;
        for (AliasTable at : aliasTable) {
            double sim = fcv.EntityTextAndObjectAlias(e.getText(), at.getAlias());
            if (sim > highestSim) {
                highestSim = sim;
            }
        }
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), highestSim);
        
        // Feature 2: The type comparison
        double typeCom = fcv.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getId());
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), typeCom);
        
        // The class
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(3), target);
        
        return thisInstance;
    }

}
