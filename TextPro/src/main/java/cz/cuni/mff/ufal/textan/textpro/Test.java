/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro;

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
    List<Long> objectListID;
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
            }
        }
    }
    Instance CreateInstance(Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO, int target) {
        // Feature 1: The similarity between entity text and object alias
        double[] values = new double[]{0};
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
        Instance instance = new DenseInstance(values, target);
        return instance;
    }
    public List<Instance> CreateTestSet(){
        List<Instance> result = new List<Instance>();
        for (ObjectTable ot:this.objectList){
            
        }
    }
    
    
}
