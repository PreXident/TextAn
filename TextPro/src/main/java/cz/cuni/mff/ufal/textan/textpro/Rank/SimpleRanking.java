package cz.cuni.mff.ufal.textan.textpro.rank;

import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import cz.cuni.mff.ufal.textan.textpro.data.LoadDatabase;
import cz.cuni.mff.ufal.textan.textpro.data.LoadTest;

import java.util.ArrayList;


public class SimpleRanking{
    
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
    int score(ArrayList<Entity> a1, ArrayList<Entity> a2) {
        int match = 0;
        for(int i1 = 0; i1 < a1.size(); i1++) {
            //System.out.println(a1.get(i1));
            String a1_i1 = a1.get(i1).getText();
            for(int i2 = 0; i2 < a2.size(); i2++){
                if(a2.get(i2).getText().equalsIgnoreCase(a1_i1)) {
                    match++;
                }
            }
        }
        
        return match;
    }
    /*
     * Top 5
     */
    public ArrayList<Entity> process(String TestDir, String DataDir) throws Exception {
        ArrayList<Entity> test = LoadTest._load(TestDir);
        SimpleRanking sr = new SimpleRanking();
        for (int num = 1; num < 3; num++) {
            ArrayList<Entity> instance = LoadDatabase._load(DataDir);
            int match = sr.score(instance, test);
            System.out.println(num + " = " + match);
        }
        return test;
        
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
