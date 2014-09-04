package cz.cuni.mff.ufal.textan.data.repositories.common;

/**
 *
 * @author Vaclav Pernicka
 */
public class DAOUtils {

    /**
     *
     * @param tableAlias alias of the table
     * @param propertyName name of the property in mapped table class
     * @return tableAlias + "." + propertyName
     */
    public final static String getAliasPropertyName(final String tableAlias, final String propertyName) {
        return tableAlias + "." + propertyName;
    }

    /**
     * get the substring pattern to like method
     *
     * @param subString string to wrap
     * @return "%" + subString + "%"
     */
    public final static String getLikeSubstring(final String subString) {
        return "%" + subString + "%";
    }
}
