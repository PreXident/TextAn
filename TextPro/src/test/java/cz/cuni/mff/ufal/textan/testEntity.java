/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan;

import cz.cuni.mff.ufal.textan.Data.Entity;
import cz.cuni.mff.ufal.textan.textpro.TextPro;
import cz.cuni.mff.ufal.textan.textpro.Running;
import java.util.List;

/**
 *
 * @author Tamhd
 */
public class testEntity {
    public static void main(String[] args) {
        TextPro ner = new Running();
        String doc = "This is not a test actually";
        List<Entity> ls = ner.recognizedEntity(doc);
        System.out.print(ls.get(0).getText());
    }    
}
