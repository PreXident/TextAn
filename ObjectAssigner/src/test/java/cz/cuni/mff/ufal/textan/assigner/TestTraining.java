package cz.cuni.mff.ufal.textan.assigner;

import cz.cuni.mff.ufal.textan.assigner.configs.ObjectAssignerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This testing package is devoted to Weka Features.
 * TODO is this doing anything?
 * @author HOANGT
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectAssignerConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TestTraining {

    @Test
    public void TestVectors() {
        Attribute StringSimilarityMaximum = new Attribute("StringSimilarityMaximum");
        Attribute StringSimilarityMinimum = new Attribute("StringSimilarityMinimum");
        Attribute StringSimilarityAverage = new Attribute("StringSimilarityAverage");
        Attribute TypeComparison = new Attribute("TypeComparison");

        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(2);
        fvClassVal.addElement("positive"); // True
        fvClassVal.addElement("negative"); // False
        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

        // Declare the feature vector
        FastVector fvWekaAttributes = new FastVector(5);
        fvWekaAttributes.addElement(StringSimilarityMaximum);
        fvWekaAttributes.addElement(StringSimilarityMinimum);
        fvWekaAttributes.addElement(StringSimilarityAverage);
        fvWekaAttributes.addElement(TypeComparison);
        fvWekaAttributes.addElement(ClassAttribute);

        Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
        isTrainingSet.setClassIndex(4);

        Instance thisInstance = new Instance(5);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), 1.0);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(1), 1.0);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(2), 1.0);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(3), 1);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(4), "positive");
        //TODO: TEST
    }
}
