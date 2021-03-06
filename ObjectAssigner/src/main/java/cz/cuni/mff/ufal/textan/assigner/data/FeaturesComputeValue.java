package cz.cuni.mff.ufal.textan.assigner.data;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import de.linuxusers.levenshtein.util.SimpleLevenshtein;
import java.util.ArrayList;
import java.util.List;

/**
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
    public static double levenshteinDistance(String entityString, String objectString) {
        //JaroWinkler algorithm = new JaroWinkler();
        //return algorithm.getSimilarity(stringA, stringB);
        return SimpleLevenshtein.getStringDistance( entityString, objectString);
    }
    /**
     * Compares the entity text and alias of object.
     * Word with one alias only
     * @param entityText entity text
     * @param aliases list of object aliases
     * @return an array of three double values
     */
    public static List<Double> entityTextAndObjectAlias(String entityText, List<String> aliases) {
        double highestSim = 0;
        double lowestSim = 1000;
        double sum = 0;
        double number = 0;
        for (String alias : aliases) {
            double sim = levenshteinDistance(entityText, alias);
            if (sim > highestSim) {
                highestSim = sim;
            }
            if(sim < lowestSim) {
                lowestSim = sim;
            }
            sum += sim;
            number += 1;
        }

        List<Double> lst = new ArrayList<>();
        lst.add(highestSim);
        lst.add(lowestSim);
        lst.add((sum+0.2)/(number+0.2)); // average
        return lst;
    }

    /**
     * Compares the type of entity and object.
     * @param eType entity type id
     * @param oType object type id
     * @return 1 if they are the same, 0 otherwise
     */
    public static double entityTypeAndObjectType(long eType, long oType) {
        if(eType == oType) {
            return 1;
        }
        return 0;
    }

    /**
     * Check if an object is the root object of joined tree
     * @param obj object to check
     * @return 1 if it is the root, 0 otherwise
     */
    public static double isRoot(ObjectTable obj) {
        double isRoot = 0;
        if (obj.isRoot()) {
            isRoot = 1;
        }
        return isRoot;
    }

    /**
     * The number of documents happens to be in both lists
     * @param doc1 first list
     * @param doc2 second list
     * @return number of documents occurring in both lists
     */
    public static double documentsOccurrenceShare(List<DocumentTable> doc1, List<DocumentTable> doc2) {
        double count = 0;
        // Iterate through document list
        for(DocumentTable dt:doc1) {
            if(doc2.contains(dt)) {
                count++;
            }
        }
        return count;
    }

    /**
     * How many documents in the document list that contain an object in the object list
     * @param doc documents
     * @param obj objects
     * @param documentTableDAO document table
     * @return number of documents containing at least one object from the object list
     */
    public static double documentsHaveObjects(List<DocumentTable> doc, List<ObjectTable> obj, IDocumentTableDAO documentTableDAO) {
        double count = 0;
        // Iterate through objects list
        for(ObjectTable o: obj) {
            List<Pair<DocumentTable,Integer>> pairDocs = documentTableDAO.findAllDocumentsWithObject(o);
            List<DocumentTable> lst = new ArrayList<>();
            for(Pair<DocumentTable,Integer> p:pairDocs) {
                lst.add(p.getFirst());
            }
            count +=  documentsOccurrenceShare(doc,lst);
        }
        return count;
    }

    /**
     * How many objects in the object list that appear in the document list
     * @param doc documents
     * @param obj objects
     * @param documentTableDAO document table
     * @return number of objects appearing in at least one document from the list
     */
    public static double objectsInDocuments(List<DocumentTable> doc, List<ObjectTable> obj, IDocumentTableDAO documentTableDAO) {
        double count = 0;
        // Iterate through objects list
        for(ObjectTable o: obj) {
            List<Pair<DocumentTable,Integer>> pairDocs = documentTableDAO.findAllDocumentsWithObject(o);
            List<DocumentTable> lst = new ArrayList<>();
            for(Pair<DocumentTable,Integer> p:pairDocs) {
                lst.add(p.getFirst());
            }
            if(documentsOccurrenceShare(doc,lst) > 0) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Utility classes need no constructor.
     */
    private FeaturesComputeValue(){
        // Initialize the class
    }
}
