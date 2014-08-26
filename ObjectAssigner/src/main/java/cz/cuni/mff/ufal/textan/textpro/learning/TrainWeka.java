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
import java.util.stream.Collectors;

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
    
    public FastVector fvWekaAttributes;
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
        fvWekaAttributes = new FastVector(5);
        fvWekaAttributes.addElement(StringSimilarityMaximum);
        fvWekaAttributes.addElement(StringSimilarityMinimum);
        fvWekaAttributes.addElement(StringSimilarityAverage);
        fvWekaAttributes.addElement(TypeComparison);
        fvWekaAttributes.addElement(ClassAttribute);
    }
    
    /*
    * Function doTraining, the same function as previous version, but different classifier
    */
    public Classifier doTraining(IObjectTableDAO objectTableDAO, IAliasTableDAO aliasTableDAO) {
        isTrainingSet = new Instances("Rel", this.fvWekaAttributes, 10);
        isTrainingSet.setClassIndex(4);
        isTrainingSet.add(CreateFakeYesInstanceBasic());
        
        // Create the list of all objects
        List<ObjectTable> objectTable = objectTableDAO.findAll();
        
        // Create YES
        for (ObjectTable obt : objectTable) {

            long type = obt.getObjectType().getId();

            //get all distinct aliases for (joined)object
            List<String> als = aliasTableDAO.findAllAliasesOfObject(obt).stream().map(AliasTable::getAlias).distinct().collect(Collectors.toList());
            if(als.isEmpty()) {
                continue;
            }
            String first_alias_text = als.get(0); //first_alias.getAlias();
            
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
        Classifier cModel = new IBk();
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
        Instance thisInstance = new Instance(5);
        
        // Compute value of instance
        //FeaturesComputeValue fcv = new FeaturesComputeValue();
        // Get all alias
        List<String> aliases = aliasTableDAO.findAllAliasesOfObject(obj).stream().map(AliasTable::getAlias).distinct().collect(Collectors.toList());

        // Feature 1: The similarity between entity text and object alias
        double highestSim = 0;
        double lowestSim = 1000;
        double sum = 0;
        double number = 0;
        for (String alias : aliases) {
            double sim = FeaturesComputeValue.EntityTextAndObjectAlias(e.getText(), alias);
            if (sim > highestSim) {
                highestSim = sim;
            }
            if(sim < lowestSim) {
                lowestSim = sim;
            }
            sum += sim;
            number += 1;
        }
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), highestSim);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(1), lowestSim);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(2), (sum+0.2)/(number+0.2));
        
        // Feature 2: The type comparison
        double typeCom = FeaturesComputeValue.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getId());
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(3), typeCom);
        
        // The class
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(4), target);
        
        return thisInstance;
    }
        
    public Instance CreateFakeYesInstanceBasic(){
        Instance thisInstance = new Instance(5);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), 100);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(1), 100);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(2), 100);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(3), 1);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(4), "positive");
        return thisInstance;
    }

}
