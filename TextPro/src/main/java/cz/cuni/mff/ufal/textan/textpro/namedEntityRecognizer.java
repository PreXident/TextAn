package cz.cuni.mff.ufal.textan.textpro;

//**************IMPLEMENT THE METHOD CALLING NER*********************

import java.util.List;
import cz.cuni.mff.ufal.textan.Data.Entity;
import java.util.ArrayList;

public class namedEntityRecognizer implements TextPro{
    
    //  Output a list of entity after processing
    public List<Entity> recognizedEntity(String document){
        List<Entity> results = new ArrayList<Entity>();
        // Do something here
        Entity en = new Entity("AAAAAAAAAAAAAAAAA", 12, 23, 1);
        results.add(en);
        return results;
    }
    
    public static void main(String[] args) {
        TextPro ner = new namedEntityRecognizer();
        String doc = "This is not a test actually";
        List<Entity> ls = ner.recognizedEntity(doc);
        System.out.print(ls.get(0).getOffset());
    }
}