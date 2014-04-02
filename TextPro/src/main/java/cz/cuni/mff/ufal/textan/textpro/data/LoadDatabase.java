/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.textpro.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Tamhd
 */
public class LoadDatabase {
    public static ArrayList<String> LoadInstance (String filedir) {
        ArrayList<String> one_instance = new ArrayList<String>();
        try {
		File fileDir = new File(filedir);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
		    //System.out.println(str);
                    one_instance.add(str);
		}
 
                in.close();
	} catch (Exception e){}
        return one_instance;
    }
    /*________________________LOAD DATA ________________________________________*/
    public static ArrayList<Entity> _load (String filedir) {
        ArrayList<Entity> one_instance = new ArrayList<Entity>();
        try {
		File fileDir = new File(filedir);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
		    //System.out.println(str);
                    one_instance.add(new Entity(str, 0, 0, 0));
		}
 
                in.close();
	} catch (Exception e){}
        return one_instance;
    }    
}
