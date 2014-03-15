/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.ufal.textan.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.javaml.core.Instance;

/**
 *
 * @author Tamhd
 */
public class LoadDatabase {
    public static Instance CreateInstance(String filedir) throws Exception {
        double[] values = new double[]{0,0}; // name match + number of matched
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
        
        return null;
    }    
    
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
    
    
}
