package cz.cuni.mff.ufal.textan.textpro;

//**************IMPLEMENT THE METHOD CALLING NER*********************

import java.util.List;
import cz.cuni.mff.ufal.textan.textpro.data.Entity;
import java.util.ArrayList;

public class Running implements ITextPro{
    
    // List of object (corresponding to the object list in database)
    List<String> objectList;
    
    // Initialize the value
    @Override
    public void init(List<String> objectlist){
        this.objectList = objectlist;
    }
    
    //  Output a list of entity after processing
    @Override
    public List<Entity> recognizedEntity(String document){
        List<Entity> results = new ArrayList<Entity>();
        // Do something here
        Entity en = new Entity("AAAAAAAAAAAAAAAAA", 12, 23, 1);
        results.add(en);
        return results;
    }
    @Override
    public List<Entity> SimpleRanking(String TestDir, String DataDir){
        /*DONT KNOW WHAT TO DO*/
        return SimpleRanking(TestDir, DataDir);
    }
    public static void main(String[] args) {
        ITextPro ner = new Running();
        String doc = "This is not a test actually";
        List<Entity> ls = ner.recognizedEntity(doc);
        System.out.print(ls.get(0).getOffset());
    }
}