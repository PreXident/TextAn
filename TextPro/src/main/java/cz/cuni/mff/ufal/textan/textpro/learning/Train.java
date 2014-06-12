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
import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 * Create training data
 * @author HOANGT
 */
public class Train {
    
    /*
     * Default training constructor
    */
    public Train(){
        
    }
    
    public Classifier doTraining(){
        Dataset data = new DefaultDataset();
        /*
         * Get data from database
        */
        data.add(new DenseInstance(new double[] {1, 1},1));
        data.add(new DenseInstance(new double[] {0, 1},0));
        data.add(new DenseInstance(new double[] {0.75, 1},1));
        data.add(new DenseInstance(new double[] {0.25, 1},0));
        data.add(new DenseInstance(new double[] {0.85, 1},1));
        data.add(new DenseInstance(new double[] {0.15, 1},0));
        
        Classifier svm = new LibSVM();
        svm.buildClassifier(data);
        return svm;
    }
    // Load dataset
    // target = 0 for no, 1 for yes 
    static Instance CreateInstance(Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO, int target) {
        // Feature 1: The similarity between entity text and object alias
        double[] values = new double[]{0,0};
        FeaturesComputeValue fcv = new FeaturesComputeValue();
        
        // Get all alias
        List<AliasTable> aliasTable = aliasTableDAO.findAllAliasesOfObject(obj);
        
        //Select the highest similarity
        // first feature value is highest Sim
        double highestSim = 0;
        for(AliasTable at:aliasTable){
            double sim = fcv.EntityTextAndObjectAlias(e.getText(), at.getAlias() );
            if(sim > highestSim) {
                highestSim = sim;
            }
        }
        values[0] = highestSim;

        // The type comparison
        double typeSim = fcv.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getName());
        values[1] = typeSim;
        
        Instance instance = new DenseInstance(values, target);
        return instance;
    }
        
        
}
