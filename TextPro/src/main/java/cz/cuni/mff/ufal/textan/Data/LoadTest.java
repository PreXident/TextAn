package cz.cuni.mff.ufal.textan.Data;

import java.io.BufferedReader;
import main.java.cz.cuni.mff.ufal.textan.ML.*;
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

public class LoadTest {
	/**
     * Shows the basic steps to create use a feature scoring algorithm.
     * 
     * @author Thomas Abeel
     * 
     */
     public static ArrayList<String> load (String filedir) throws Exception {
        ArrayList<String> test_instance = new ArrayList<String>();
        String text = "";
        try {
		File fileDir = new File(filedir);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
		    //System.out.println(str);
                    text += str.toLowerCase();
		}
 
                in.close();
	} catch (Exception e){}

        //System.out.println(text);
        String pattern = "<ne type=\"(.*?)\">(.*?)</ne>";
        Pattern p = Pattern.compile(pattern);
        ArrayList<String> list = new ArrayList<String>(); 
        Matcher matcher = p.matcher(text);
        String firstname = "";
        String surname = "";
        while(matcher.find()){
            String type = matcher.group(1).replaceAll("<ne type=\".*\">", "");
            String value = matcher.group(2).replaceAll("<ne type=\".*\">", "");
            test_instance.add(value);
            if (list.indexOf(type) == -1) {
                //System.out.println(type + " = " + value);        
            }
            if (type.substring(0,1).equalsIgnoreCase("p")) {
                list.add(type);
                if(type.equalsIgnoreCase("ps")) {
                    surname = value;
                }
                if(type.equalsIgnoreCase("p")) {
                    firstname = value;
                }
            }
        }
        test_instance.add(firstname + " " + surname);
        //System.out.println("Name: " + firstname + " " + surname); 
        return test_instance;
    }

    public static void main(String[] args) throws Exception {
        String text = "";
        try {
		File fileDir = new File("data/sample2.entities.txt");
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
		    //System.out.println(str);
                    text += str;
		}
 
                in.close();
	} catch (Exception e){}

        //System.out.println(text);
        String pattern = "<ne type=\"(.*?)\">(.*?)</ne>";
        Pattern p = Pattern.compile(pattern);
        ArrayList<String> list = new ArrayList<String>(); 
        Matcher matcher = p.matcher(text);
        String firstname = "";
        String surname = "";
        while(matcher.find()){
            String type = matcher.group(1).replaceAll("<ne type=\".*\">", "");
            String value = matcher.group(2).replaceAll("<ne type=\".*\">", "");
            
            if (list.indexOf(type) == -1) {
                System.out.println(type + " = " + value);        
            }
            if (type.substring(0,1).equalsIgnoreCase("p")) {
                list.add(type);
                if(type.equalsIgnoreCase("ps")) {
                    surname = value;
                }
                if(type.equalsIgnoreCase("p")) {
                    firstname = value;
                }
            }
        }
        
        System.out.println("Name: " + firstname + " " + surname); 
    }
}
