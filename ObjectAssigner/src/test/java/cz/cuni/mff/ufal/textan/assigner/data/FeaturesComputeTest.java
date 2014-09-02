package cz.cuni.mff.ufal.textan.assigner.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Purpose: test the function which compute the value of features
 * @author HOANGT
 */
public class FeaturesComputeTest {

    /**
     * Test the similarity metrics between two strings
     */
    @Test
    public void testLevenshteinDistance() {
        
        double perfect = FeaturesComputeValue.levenshteinDistance("xxxxx", "xxxxx");
        assertTrue("Two exact string has the difference 0", perfect == 0);
        
        String entityText = "John Jr.";
        String objectAlias = "John";
        double diff = FeaturesComputeValue.levenshteinDistance(entityText, objectAlias);
        boolean check = (diff > 0);
        assertTrue("The similarity is greater than 0", check);
        
        String entityText2 = "Mary Summer";
        double diff2 = FeaturesComputeValue.levenshteinDistance(entityText2, objectAlias);
        boolean check3 = (diff2 > 10);
        assertTrue("John vs Mary Summer are different", check3);        
        boolean check2 = (diff < diff2);
        assertTrue("John Jr. is more similar to John than Mary Summer", check2);
    }

    /**
     * Test if an entity and an object have the same type
     */
    @Test
    public void testEntityTypeAndObjectType() {
        long entityType = 1;
        long objectType = 1;
        double comp = FeaturesComputeValue.entityTypeAndObjectType(entityType, objectType);
        boolean check = (comp == 1);
        assertTrue("Two types are the same", check);
        
        long objectType2 = 12;
        double comp2 = FeaturesComputeValue.entityTypeAndObjectType(entityType, objectType2);
        boolean check2 = (comp2 == 0);
        assertTrue("Two types are different", check2);
        
    }
}
