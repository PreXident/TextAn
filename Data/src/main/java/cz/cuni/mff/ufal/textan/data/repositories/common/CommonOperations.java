/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.common;

/**
 *
 * @author Václav Pernička
 */
public class CommonOperations {

    public final static String getAliasPropertyName(final String tableAlias, final String propertyName) {
        return tableAlias + "." + propertyName;
    }    

    public final static String getLikeSubstring(final String subString) {
        return "%" + subString + "%";
    }
}
