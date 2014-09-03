package cz.cuni.mff.ufal.textan.assigner.learning;

import cz.cuni.mff.ufal.textan.assigner.data.Entity;
import cz.cuni.mff.ufal.textan.assigner.data.FeaturesComputeValue;
import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IAliasTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IDocumentTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.AliasTable;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
/**
 * A class which performs: create instance and learn
 * @author HOANGT
 */
public class TrainWeka {

    public FastVector fvWekaAttributes;
    public Instances isTrainingSet;

    /**
     * Only constructor.
     */
    public TrainWeka() {

        // Attribute = Features
        Attribute StringSimilarityMaximum = new Attribute("StringSimilarityMaximum");
        Attribute StringSimilarityMinimum = new Attribute("StringSimilarityMinimum");
        Attribute StringSimilarityAverage = new Attribute("StringSimilarityAverage");
        Attribute TypeComparison = new Attribute("TypeComparison");
        Attribute IsRoot = new Attribute("IsRoot");
        Attribute NeighborsNumber = new Attribute("NeighborsNumber");
        Attribute DocOccurrences1 = new Attribute("DocOccurrences1");
        Attribute DocOccurrences2 = new Attribute("DocOccurrences2");
        Attribute EntityDocuments = new Attribute("EntityDocuments");
        Attribute ShareDocuments = new Attribute("ShareDocuments");
        Attribute DocumentHave = new Attribute("DocumentHave");
        Attribute ObjectInDoc = new Attribute("ObjectInDoc");

        // Declare the class attribute along with its values
        FastVector fvClassVal = new FastVector(2);
        fvClassVal.addElement("positive"); // True
        fvClassVal.addElement("negative"); // False
        Attribute ClassAttribute = new Attribute("theClass", fvClassVal);


        // Declare the feature vector
        fvWekaAttributes = new FastVector(13);
        fvWekaAttributes.addElement(StringSimilarityMaximum);
        fvWekaAttributes.addElement(StringSimilarityMinimum);
        fvWekaAttributes.addElement(StringSimilarityAverage);
        fvWekaAttributes.addElement(TypeComparison);
        fvWekaAttributes.addElement(IsRoot);
        fvWekaAttributes.addElement(NeighborsNumber);
        fvWekaAttributes.addElement(DocOccurrences1);
        fvWekaAttributes.addElement(DocOccurrences2);
        fvWekaAttributes.addElement(EntityDocuments);
        fvWekaAttributes.addElement(ShareDocuments);
        fvWekaAttributes.addElement(DocumentHave);
        fvWekaAttributes.addElement(ObjectInDoc);
        fvWekaAttributes.addElement(ClassAttribute);
    }

    /**
     * Function doTraining, the same function as previous version, but different classifier.
     * @param objectTableDAO object table
     * @param aliasTableDAO alias table
     * @param documentTableDAO document table
     * @return classifier
     */
    public Classifier doTraining(final IObjectTableDAO objectTableDAO,
            final IAliasTableDAO aliasTableDAO,
            final IDocumentTableDAO documentTableDAO) {
        isTrainingSet = new Instances("Rel", this.fvWekaAttributes, 10000);
        isTrainingSet.setClassIndex(12);
        // Create an artificial instance as the first point
        isTrainingSet.add(createArtificialInstance());

        // Create the list of all objects
        final List<ObjectTable> objectTable = objectTableDAO.findAll();
        for (ObjectTable obt : objectTable) {

            final long type = obt.getObjectType().getId();

            //get all distinct aliases for (joined)object
            final List<String> als =
                    aliasTableDAO.findAllAliasesOfObject(obt).stream()
                            .map(AliasTable::getAlias)
                            .distinct()
                            .collect(Collectors.toList());
            if(als.isEmpty()) {
                continue;
            } else {
                for (String alias_text : als) {
                    // Create an artificial entity for training yes
                    Entity e = new Entity(alias_text, type);
                    // Create another artificial entity for training no
                    Entity e_reverse = new Entity(new StringBuilder(alias_text).reverse().toString(), type - 1);
                    // create an instance from this pair
                    Instance ins = createInstance(e, obt, aliasTableDAO, objectTableDAO, documentTableDAO, "positive");
                    Instance ins_reverse = createInstance(e_reverse, obt, aliasTableDAO, objectTableDAO, documentTableDAO, "negative");
                    // Add the instance to dataset
                    isTrainingSet.add(ins);
                    isTrainingSet.add(ins_reverse);
                }
            }
        }

        // Create a model
        Classifier cModel = new IBk();
        try {
            cModel.buildClassifier(isTrainingSet);
        } catch (Exception ex) {
            Logger.getLogger(TrainWeka.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cModel;
    }
    /**
     * Create an instance for the dataset
     * @param e entity
     * @param obj object
     * @param aliasTableDAO alias table
     * @param objectTableDAO object table
     * @param documentTableDAO document table
     * @param target positive or negative
     * @return a real instance
     */
    public Instance createInstance
        (Entity e, ObjectTable obj, IAliasTableDAO aliasTableDAO,
                IObjectTableDAO objectTableDAO,
                IDocumentTableDAO documentTableDAO,
                String target) {

        // Instance
        final Instance thisInstance = new Instance(13);

        // Get all alias
        final List<String> aliases =
                aliasTableDAO.findAllAliasesOfObject(obj).stream()
                        .map(AliasTable::getAlias)
                        .distinct()
                        .collect(Collectors.toList());

        // Feature 0,1,2: The similarity between entity text and object alias
        List<Double> textVSalias = FeaturesComputeValue.entityTextAndObjectAlias(e.getText(), aliases);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), textVSalias.get(0)); // highest
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(1), textVSalias.get(1)); // lowest
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(2), textVSalias.get(2)); // average

        // Feature 3: The type comparison
        double typeCom = FeaturesComputeValue.entityTypeAndObjectType(e.getType(), obj.getObjectType().getId());
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(3), typeCom);

        // Feature 4: If an object is the root of joined objects, the chance for root is higher
        double isRoot = FeaturesComputeValue.isRoot(obj);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(4), isRoot);

        // Feature 5: Number of neighbors an object has
        List<Pair<ObjectTable, RelationTable>> neighborsAndRelations =  objectTableDAO.getNeighbors(obj);
        double neighbor = neighborsAndRelations.size();
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(5), neighbor);
        // Convert to a list of neighbors, no need for relation name
        List<ObjectTable> neighborsList = new ArrayList<>();
        for(Pair<ObjectTable, RelationTable> p:neighborsAndRelations) {
            neighborsList.add(p.getFirst());
        }


        // Feature 6,7: Number of document occurences that the object appears
        List<Pair<DocumentTable, Integer>> documentIntergerList = documentTableDAO.findAllDocumentsWithObject(obj);
        double numDocs = neighborsList.size();
        // count number of docs
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(6), numDocs);
        double sumDocs = 0;
        List<DocumentTable> documentList2 = new ArrayList<>();
        for(Pair<DocumentTable, Integer> p:documentIntergerList) {
            sumDocs += p.getSecond();
            documentList2.add(p.getFirst());
        }
        // sum all the occurrences of aliases
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(7), sumDocs);

        // Feature 8: Number of documents and Entity appear in the documents
        List<DocumentTable> entityDocumentList = documentTableDAO.findAllDocumentsByFullText(e.getText());
        double enDocs = entityDocumentList.size();
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(8), enDocs);

        // Feature 9: Number of share documents between the entity and object
        double enShareDocs = FeaturesComputeValue.documentsOccurrenceShare(entityDocumentList, documentList2);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(9), enShareDocs);

        // Feature 10: Number of documents having both entity and object neighbor
        double docsHave = FeaturesComputeValue.documentsHaveObjects(entityDocumentList, neighborsList, documentTableDAO);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(10), docsHave);

        // Feature 11: Number of neighbors happening to be in the same document with the entity
        double objectIn = FeaturesComputeValue.objectsInDocuments(entityDocumentList, neighborsList, documentTableDAO);
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(11), objectIn);

        // The class
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(12), target);

        // return the final instance
        return thisInstance;
    }

    /**
     * Create a sample instance from a list
     * @param attributes list of attributes
     * @param target positive or negative
     * @return a sample instance from a list
     */
    public Instance createInstanceFromList(List<Double> attributes, String target){
        Instance thisInstance = new Instance(attributes.size() + 1);
        for(int attID = 0; attID < attributes.size(); attID++) {
            thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(attID), attributes.get(attID));
        }
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(attributes.size()), target);
        return thisInstance;
    }
    /**
     * Create a sample instance from an array
     * @param attributes array of attributes
     * @param target positive or negative
     * @param size size
     * @return an instance
     */
    public Instance createInstanceFromArray(double[] attributes, String target, int size){
        Instance thisInstance = new Instance(size);
        for(int attID = 0; attID < size -1 ; attID++) {
            thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(attID), attributes[attID]);
        }
        thisInstance.setValue((Attribute)fvWekaAttributes.elementAt(size - 1), target);
        return thisInstance;
    }

    /**
     * Create a static artificial instance to make sure the training dataset never empty
     * @return an instance
     */
    public Instance createArtificialInstance(){
        double[] attributes = new double[]{0, 10, 0.5, 1, 1, 1, 1, 1, 1, 1, 2, 3};
        return createInstanceFromArray(attributes, "positive", 13);
    }

}
