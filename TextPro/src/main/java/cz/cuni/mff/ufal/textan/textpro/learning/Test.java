/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro.learning;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.FeaturesComputeValue;
import java.util.ArrayList;
import java.util.List;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

/**
 *
 * @author HOANGT
 */
public class Test {

    List<ObjectTable> objectList;

    public List<ObjectTable> getObjectList() {
        return objectList;
    }

    public List<Long> getObjectListID() {
        return objectListID;
    }
    List<Long> objectListID;
    
    List<Double> objectListScore;

    public List<Double> getObjectListScore() {
        return objectListScore;
    }
    /*
    * Constructor
    */
    public Test(Entity _e, List<ObjectTable> oList, List<Long> oListID, Double[] score, double minscore) {
        this.objectList = new ArrayList<ObjectTable>();
        this.objectListID = new ArrayList<Long>();

        int size = oList.size();
        for (int i = 0; i < size; i++) {
            if (score[i] >= minscore) {
                this.objectList.add(oList.get(i));
                this.objectListID.add(oListID.get(i));
                this.objectListScore.add(score[i]);
            }
        }
    }
    Instance CreateInstance(Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO, int target) {
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
        
        // Find the type similarity
        double typeSim = fcv.EntityTypeAndObjectType(e.getType(), obj.getObjectType().getName());
        values[1] = typeSim;
        
        Instance instance = new DenseInstance(values, target);
        return instance;
    }
    public List<Instance> CreateTestSet(Entity e, IAliasTableDAO aliasTableDAO){
        List<Instance> result = new ArrayList<Instance>();
        for (ObjectTable ot:this.objectList){
            Instance ins = CreateInstance(e, ot, aliasTableDAO, 1);
            result.add(ins);
        }
        return result;
    }
    
    
}
