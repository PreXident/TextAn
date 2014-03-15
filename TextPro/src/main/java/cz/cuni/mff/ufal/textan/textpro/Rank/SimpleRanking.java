package cz.cuni.mff.ufal.textan.textpro.Rank;

import cz.cuni.mff.ufal.textan.Data.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.*;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.featureselection.scoring.GainRatio;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.featureselection.ensemble.LinearRankingEnsemble;
import net.sf.javaml.tools.data.FileHandler;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import net.sf.javaml.tools.weka.WekaAttributeSelection;

public class SimpleRanking {
    
    int array_compare(ArrayList<String> a1, ArrayList<String> a2) {
        int match = 0;
        for(int i1 = 0; i1 < a1.size(); i1++) {
            //System.out.println(a1.get(i1));
            if(a2.indexOf(a1.get(i1)) != -1) {
                match++;
            }
        }
        
        return match;
    }
    /**
     * Shows the basic steps to create use a feature scoring algorithm.
     * 
     * @author Thomas Abeel
     * 
     */
    public static void main(String[] args) throws Exception {
        ArrayList<String> test = LoadTest.load("data/sample1.entities.txt");
        SimpleRanking sr = new SimpleRanking();
        for (int num = 1; num < 3; num++) {
            ArrayList<String> instance = LoadDatabase.LoadInstance("data/database/" + num + ".txt");
            int match = sr.array_compare(instance, test);
            System.out.println(num + " = " + match);
        }
    }
}
