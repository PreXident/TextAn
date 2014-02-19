/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.test;

import cz.cuni.mff.ufal.textan.data.repositories.Data;

/**
 *
 * @author Václav Pernička
 */
public class DataSingleton {
    private static final Data singleton = new Data();

    public static Data getSingleton() {
        return singleton;
    }
    
}
