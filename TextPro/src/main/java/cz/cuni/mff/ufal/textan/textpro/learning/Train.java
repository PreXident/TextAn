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
import java.util.List;
import java.util.Set;
import javafx.util.Pair;
import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 * Create training data
 *
 * @author HOANGT
 */
public class Train {

    /*
     * Default training constructor
    */
    public Train() {

    }

    /*
    * Training main function
    * Other class should call this function to build the model (in short: TO LEARN)
    */
    public Classifier doTraining(IObjectTableDAO objectTableDAO, IAliasTableDAO aliasTableDAO) {

        Dataset data = new DefaultDataset();

        // Create the list of all objects
        List<ObjectTable> objectTable = objectTableDAO.findAll();

        //ArrayList<Pair> train_pair = new ArrayList<Pair>();


        // Create list of pair between entity and object - YES 
        for (ObjectTable obt : objectTable) {

            long type = obt.getObjectType().getId(); //TODO: why you use name instead of identifier?
            Set<AliasTable> als = obt.getAliases();
            AliasTable first_alias = als.iterator().next();
            String first_alias_text = ""; //first_alias.getAlias();

            // Create a fake entity for training
            Entity e = new Entity(first_alias_text, 0, 0, type);
            Pair<Entity, ObjectTable> p = new Pair<Entity, ObjectTable>(e, obt);

            // create an instance from this pair
            Instance ins = CreateInstanceBasic(e, obt, aliasTableDAO, objectTableDAO, 1);

            // Add the instance to dataset
            data.add(ins);
        }

        Classifier knn = new KNearestNeighbors(5);
        knn.buildClassifier(data);
        return knn;
    }

    /*
     * Create learning model with just a few features
     *    
    */
    static Instance CreateInstanceBasic(Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO, IObjectTableDAO objectTableDAO, int target) {

        // Create a defult instance
        double[] values = new double[]{1, 1, 1, 1};
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
        values[0] = highestSim;

        // Feature 2: The type comparison
        double typeSim = fcv.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getId());
        values[1] = typeSim;

        // Feature 3: Popularity of object
        double components = fcv.NumberOfComponentObject(obj);
        values[2] = components;

        return new DenseInstance(values, target);
    }

    // Load dataset
    // target = 0 for no, 1 for yes 
    static Instance CreateInstance(String doc, Entity e, List<Entity> eList, ObjectTable obj, IAliasTableDAO aliasTableDAO, IObjectTableDAO objectTableDAO, int target) {
        // Feature 1: The similarity between entity text and object alias
        double[] values = new double[]{1, 1, 1, 1};
        FeaturesComputeValue fcv = new FeaturesComputeValue();

        // Get all alias
        List<AliasTable> aliasTable = aliasTableDAO.findAllAliasesOfObject(obj);

        //Feature 1: Select the highest similarity
        double highestSim = 0;
        for (AliasTable at : aliasTable) {
            double sim = fcv.EntityTextAndObjectAlias(e.getText(), at.getAlias());
            if (sim > highestSim) {
                highestSim = sim;
            }
        }
        values[0] = highestSim;

        // compare the type of entity and object
        double typeSim = fcv.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getId());
        values[1] = typeSim;


        // The mutual object
        double mutual = fcv.EntityAndObjectMutual(doc, eList, e, obj, objectTableDAO);
        values[2] = mutual;

        // Popularity of object
        double components = fcv.NumberOfComponentObject(obj);
        values[3] = components;

        return new DenseInstance(values, target);
    }
}
