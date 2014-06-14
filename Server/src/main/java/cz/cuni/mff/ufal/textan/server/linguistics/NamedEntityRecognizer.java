package cz.cuni.mff.ufal.textan.server.linguistics;

import cz.cuni.mff.ufal.nametag.*;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.ObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A named entity recognizer.
 * Internally use NameTag tool.
 *
 * @author Jakub Vlček
 * @see <a href="http://ufal.mff.cuni.cz/nametag">NameTag page</a>
 */
public class NamedEntityRecognizer {

    private static final Logger LOG = LoggerFactory.getLogger(NamedEntityRecognizer.class);

    private static final String TRAIN_NER = "train_ner"; //TODO: move binaries into bin directory?

    private static final String MODELS_DIR = "models";
    private static final String MODEL_FILE_EXTENSION = ".ner";
    private static final String MODEL_FILE_PREFIX = "model";

    private final IObjectTypeTableDAO objectTypeTableDAO;
    private final Hashtable<Long, ObjectType> idTempTable;

    private Ner ner;

    public NamedEntityRecognizer(IObjectTypeTableDAO objectTypeTableDAO) {
        this.objectTypeTableDAO = objectTypeTableDAO;
        idTempTable = new Hashtable<>();
    }

    /**
     * Initialize NameTag
     * if there are existing models, than use newest one, else train new
     */
    public void init() {
        LOG.info("Initializing NameTag");

        LOG.info("Looking for models");
        File modelsDir = new File(MODELS_DIR);
        if (modelsDir.exists() && modelsDir.isDirectory()) {

            FilenameFilter modelsFilter = (dir, name) -> (name.length() > MODEL_FILE_EXTENSION.length()) && (name.endsWith(MODEL_FILE_EXTENSION));
            File[] models = modelsDir.listFiles(modelsFilter);

            if (models.length > 0) {
                Arrays.sort(models, (File a, File b) -> Long.signum(b.lastModified() - a.lastModified()));
                LOG.info("Existing model(s) found)");
                int i = 0;
                while ((i < models.length) && (!bindModel(models[i]))) {
                    ++i;
                }
                if (i >= models.length) {
                    LOG.info("Found models are corrupted, learning");
                    learn(true);
                }
            } else {
                LOG.info("No models found");
                learn(true);
            }
        } else {
            LOG.warn("Directory {} not exists", modelsDir); //FIXME
        }

    }

    /**
     * changing model of NameTag
     *
     * @param pathToModel path to *.ner file
     * @return true if change was successful, else false
     */
    private boolean bindModel(File pathToModel) {
        if (!pathToModel.exists()) {
            LOG.error("Model {} wasn't found", pathToModel.getAbsolutePath());
            return false;
        }

        LOG.info("Changing model");
        Ner tempNer = Ner.load(pathToModel.getAbsolutePath());
        if (tempNer == null) {
            LOG.error("Model {} is corrupted", pathToModel.getAbsolutePath());
            return false;
        } else {
            ner = tempNer;
            LOG.info("Model changed to {}", pathToModel.getAbsolutePath());
        }

        return true;
    }

    /**
     * Function that creates commands for learning new NameTag model.
     *
     * @return string array with commands
     */
    private List<String> prepareLearningArguments(File workingDirectory) {
        String[] configValues = {"czech", "morphodita:czech-131112-pos_only.tagger", "features-tsd13.txt", "2", "30", "-0.1", "0.1", "0.01", "0.5", "0", ""}; //TODO: move to default property file?
        String[] configNames = {"ner_identifier", "tagger", "featuresFile", "stages", "iterations", "missing_weight", "initial_learning_rage", "final_learning_rage", "gaussian", "hidden_layer", "heldout_data"};

        List<String> result = new LinkedList<>();
        result.add(new File(workingDirectory, mapBinaryName(TRAIN_NER)).toString()); //TODO: test if file exists? (IOException?)

        try (InputStream configFileStream = NamedEntityRecognizer.class.getResource("/NametagLearning.properties").openStream()){ //TODO: default(inside jar) and user properties?
            Properties p = new Properties();
            p.load(configFileStream);

            for (int i = 0; i < configNames.length; ++i) {
                try {
                    String value = (String) p.get(configNames[i]);
                    if (value != null) {
                        configValues[i] = value;
                    } else {
                        LOG.warn("Config value {} wasn't set, using default value.", configNames[i]);
                    }
                } catch (Exception e) {
                    LOG.warn("Config value " + configNames[i] + " wasn't set, using default value.", e);
                } finally {
                    if (!configValues[i].isEmpty()) {
                        result.add(configValues[i]);
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Config file for NameTag wasn't found, using default values.", e);
            for (int i = 0; i < configNames.length; ++i) {
                if (!configValues[i].isEmpty()) {
                    result.add(configValues[i]);
                }
            }
        }
        //result[result.length - 1] = command.toString();
        return result;
    }

    /**
     * Learn new model
     *
     * @param waitForModel true when learning is tu be blocking, else false
     */
    //TODO: only package private and move the "learn" command class into this package?
    public void learn(boolean waitForModel) {
        LOG.info("Started training new NameTag model");
        try {
            File dir = new File(Paths.get("../../Linguistics/training").toAbsolutePath().toRealPath().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SSS");
            File modelLocation = new File(MODELS_DIR, MODEL_FILE_PREFIX + sdf.format(Calendar.getInstance().getTime()) + MODEL_FILE_EXTENSION).getAbsoluteFile();
            LOG.debug("New model path: {}", modelLocation);

            List<String> learningCommand = prepareLearningArguments(dir);
            LOG.debug("Executing learning command: {}", String.join(" ", learningCommand));

            // build process
            ProcessBuilder pb = new ProcessBuilder(learningCommand);
            File trainingDataFile = new File(dir.getAbsolutePath() + File.separator + "cnec2.0-all" + File.separator + "train.txt"); //TODO: move to config, why the restriction for the dir?
            pb.directory(dir);
            // IO redirection
            pb.redirectInput(trainingDataFile);
            pb.redirectOutput(modelLocation);
            pb.redirectErrorStream(false);
            Process ps = pb.start();

            boolean notTimeout= true;
            if (waitForModel) {
                LOG.info("Waiting for training process");
                notTimeout = ps.waitFor(5, TimeUnit.MINUTES); //TODO: timeout in configuration?
            }

            if ((notTimeout) && (ps.exitValue() == 0)) {
                LOG.info("Training done");
                this.bindModel(modelLocation);
            } else {
                //FIXME is error only last line?
                BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
                String errorMsg = null;
                String line;
                while ((line = reader.readLine()) != null) {
                    errorMsg = line;
                }

                //TODO: throw some error?
                LOG.error("Training failed: exit code: {}, error message: {}", ps.exitValue(), errorMsg);
            }

        } catch (IOException e) {
            LOG.error("Training failed", e);
        } catch (InterruptedException e) {
            LOG.error("Training takes too long", e);
        }
    }

    /**
     * Translate entity type from string to Object Type
     *
     * @param entityType entity type decoded by NameTag
     * @return translated entity type
     */
    ObjectType translateEntity(String entityType) {
        ObjectType value = new ObjectType(-1L, "");
        Long id = -1L;
        try {
            id = Long.parseLong(entityType);
        } catch (NumberFormatException nfe) {
            // log outside of method FIXME
        }
        if (id == -1L) {
            return null;
        }
        if (idTempTable.containsKey(id)) {
            value = idTempTable.get(id);
            LOG.debug("Using CACHED entity {}", value.getName());
        } else {
//            try { FIXME: try block? why? catching Exception is too strong!
                ObjectTypeTable tableObject = objectTypeTableDAO.find(id);
                if (tableObject != null) {
                    value = new ObjectType(tableObject.getId(), tableObject.getName());
                    idTempTable.put(id, value);
                    LOG.debug("Using DATABASE entity {}", value.getName());
                } else {
                    LOG.warn("Entity type {} recognized, but is not stored in database.", entityType);
                }
//            } catch (Exception ex) {
//                LOG.warn("Exception occurred when trying translate entity.", ex);
//            }
        }
        return value;
    }

    public List<Entity> tagText(String input) {
        LOG.debug(input);
        if (ner == null) {
            LOG.error("NameTag hasn't model!");
            return new ArrayList<>();
        }
        Forms forms = new Forms();
        TokenRanges tokens = new TokenRanges();
        NamedEntities entities = new NamedEntities();
        ArrayList<NamedEntity> sortedEntities = new ArrayList<>();
        Scanner reader = new Scanner(input);
        List<Entity> entitiesList = new ArrayList<>();
        Stack<NamedEntity> openEntities = new Stack<>();
        Tokenizer tokenizer = ner.newTokenizer();
        boolean notEof = true;
        while (notEof) {
            StringBuilder textBuilder = new StringBuilder();
            String line;

            // Read block
            while (notEof = reader.hasNextLine()) {
                line = reader.nextLine();
                textBuilder.append(line);
                textBuilder.append('\n');
            }
            textBuilder.append('\n');

            // Tokenize and recognize
            String text = textBuilder.toString();
            tokenizer.setText(text);

            while (tokenizer.nextSentence(forms, tokens)) {
                ner.recognize(forms, entities);
                sortEntities(entities, sortedEntities);

                for (int i = 0, e = 0; i < tokens.size(); i++) {

                    for (; e < sortedEntities.size() && sortedEntities.get(e).getStart() == i; e++) {
                        openEntities.push(sortedEntities.get(e));
                    }

                    while (!openEntities.empty() && (openEntities.peek().getStart() + openEntities.peek().getLength() - 1) == i) {
                        NamedEntity endingEntity = openEntities.peek();
                        int entityStart = (int) tokens.get((int) (i - endingEntity.getLength() + 1)).getStart();
                        int entityEnd = (int) (tokens.get(i).getStart() + tokens.get(i).getLength());
                        if (openEntities.size() == 1) {
                            ObjectType recognizedEntity = translateEntity(endingEntity.getType());
                            if (recognizedEntity != null) {
                                LOG.debug("Recognized entity: {}", encodeEntities(text.substring(entityStart, entityEnd)));
                                entitiesList.add(new Entity(encodeEntities(text.substring(entityStart, entityEnd)), entityStart, entityEnd - entityStart - 1, recognizedEntity));
                            } else {
                                LOG.debug("Type {} of entity {} recognized by NameTag, but is not in database.", endingEntity.getType(), encodeEntities(text.substring(entityStart, entityEnd)));
                            }

                        }
                        openEntities.pop();
                    }
                }
            }
        }

        return entitiesList;
    }

    private void sortEntities(NamedEntities entities, ArrayList<NamedEntity> sortedEntities) {
        class NamedEntitiesComparator implements Comparator<NamedEntity> {
            public int compare(NamedEntity a, NamedEntity b) {
                if (a.getStart() < b.getStart()) return -1;
                if (a.getStart() > b.getStart()) return 1;
                if (a.getLength() > b.getLength()) return -1;
                if (a.getLength() < b.getLength()) return 1;
                return 0;
            }
        }
        NamedEntitiesComparator comparator = new NamedEntitiesComparator();

        sortedEntities.clear();
        for (int i = 0; i < entities.size(); i++)
            sortedEntities.add(entities.get(i));
        Collections.sort(sortedEntities, comparator);
    }

    private String encodeEntities(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    private static String mapBinaryName(String binName) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return binName + ".exe";
        } else {
            return binName;
        }
    }
}