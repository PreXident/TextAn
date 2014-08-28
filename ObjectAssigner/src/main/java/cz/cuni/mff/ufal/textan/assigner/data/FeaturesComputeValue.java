package cz.cuni.mff.ufal.textan.assigner.data;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import de.linuxusers.levenshtein.util.SimpleLevenshtein;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO is this ready? Most of it is never used!!!
 * @author HOANGT
 */
public class FeaturesComputeValue {

    /**
     * Compares the entity text and alias of object.
     * Word with one alias only
     * @param entityString entity text
     * @param objectString object alias
     * @return Levenshtein distance
     */
    public static double EntityTextAndObjectAlias(String entityString, String objectString) {
        //JaroWinkler algorithm = new JaroWinkler();
        //return algorithm.getSimilarity(stringA, stringB);
        return SimpleLevenshtein.getStringDistance( entityString, objectString);
    }


    /**
     * Compares the type of entity and object.
     * @param eType entity type id
     * @param oType object type id
     * @return 1 if they are the same, 0 otherwise
     */
    public static double EntityTypeAndObjectType(long eType, long oType) {
        if(eType == oType) {
            return 1;
        }
        return 0;
    }

    /**
     * Get the Mutual object.
     * TODO I do not understand the language of your tribe!
     * @param text
     * @param eList
     * @param e
     * @param o
     * @param objectTableDAO
     * @return
     */
    public static double EntityAndObjectMutual(String text, List<Entity> eList, Entity e, ObjectTable o, IObjectTableDAO objectTableDAO) {

        // List of all object table associated with OTHER entities
        final List<ObjectTable> finalDocList = new ArrayList<>();
        for (Entity e_other:eList) {
            if(e_other.getText().equalsIgnoreCase(e.getText())) {
                continue;
            }
            List<ObjectTable> oList1 = getMutualObject(e_other, objectTableDAO); // List of object closed to the entity
            for(ObjectTable o2:oList1) {
                finalDocList.add(o2);
            }
        }
        // List of all object table which the Object is joined from
        final Set<ObjectTable> oList2 = o.getObjectsThisIsJoinedFrom();

        double count = 0.00; // number of Mutual
        for (ObjectTable sample:finalDocList) {
            if(oList2.contains(sample)) {
                count ++;
            }
        }

        return count;
    }

    /**
     * TODO finish javadoc!!!
     * @param e
     * @param objectTableDAO
     * @return
     */
    public static List<ObjectTable> getMutualObject(Entity e, IObjectTableDAO objectTableDAO){
        return objectTableDAO.findAllByAliasSubstring(e.getText());
    }

    /**
     * Returns number of objects that given object is joined to.
     * Maybe important.
     * @param o object
     * @return number of objects that given object is joined to
     */
    public static double NumberOfComponentObject( ObjectTable o) {
        final Set<ObjectTable> oList2 = o.getObjectsThisIsJoinedFrom();
        return oList2.size();
    }

    /**
     * Utility classes need no constructor.
     */
    private FeaturesComputeValue(){
        // Initialize the class
    }
}
