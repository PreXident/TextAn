package cz.cuni.mff.ufal.textan.server.nametagIntegration;

import cz.cuni.mff.ufal.nametag.*;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTypeTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import cz.cuni.mff.ufal.textan.server.models.Entity;
import cz.cuni.mff.ufal.textan.server.models.ObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jakub Vlcek on 29. 4. 2014.
 */
public class NameTagServices {
    private Ner ner;
    private static final Logger LOG = LoggerFactory.getLogger(NameTagServices.class);
    Hashtable<Long, ObjectType> idTempTable;
    IObjectTypeTableDAO objectTypeTableDAO;


    public NameTagServices(IObjectTypeTableDAO objectTypeTableDAO) {
        this.objectTypeTableDAO = objectTypeTableDAO;
        idTempTable =  new Hashtable<Long, ObjectType>();
    }

    /**
     * Initialize nametag
     * if there are existing models, than use newest one, else train new
     */
    public void init() {
        LOG.info("Initializing nametag");
        LOG.info("Looking for models");
        File modelsDir = new File("models");
        if (modelsDir.exists() && modelsDir.isDirectory()) {
            FilenameFilter modelsFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.lastIndexOf('.')>0)
                    {
                        // get last index for '.' char
                        int lastIndex = name.lastIndexOf('.');

                        // get extension
                        String str = name.substring(lastIndex);

                        // match path name extension
                        if(str.equals(".ner"))
                        {
                            return true;
                        }
                    }
                    return false;
                }
            };
            File[] models = modelsDir.listFiles(modelsFilter);
            if (models.length > 0) {
                Arrays.sort(models, new ModelsComparator());
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

        }
    }

    /**
     * changing model of nametag
     * @param pathToModel path to *.ner file
     * @return true if change was successful, else false
     */
    private boolean bindModel(File pathToModel) {
        if (!pathToModel.exists()) {
            LOG.error("Model " + pathToModel.getAbsolutePath() + " wasn't found");
            return false;
        }
        LOG.info("Changing model");
        Ner tempNer = Ner.load(pathToModel.getAbsolutePath());
        if (tempNer == null) {
            LOG.error("Model " + pathToModel.getAbsolutePath() + " is corrupted");
            return false;
        }
        else {
            ner = tempNer;
            LOG.info("Model changed to " + pathToModel.getAbsolutePath());
        }
        return true;
    }

    /**
     * Function that creates commands for learning new nametag model.
     * @return string array with commands
     */
    private String[] prepareLearningArguments(File outputFilePath) {
        String[] configValues = {"czech", "morphodita:czech-131112-pos_only.tagger", "features-tsd13.txt", "2","30", "-0.1", "0.1", "0.01", "0.5", "0", ""};
        String[] configNames = {"ner_identifier", "tagger", "featuresFile", "stages", "iterations", "missing_weight", "initial_learning_rage", "final_learning_rage", "gaussian", "hidden_layer", "heldout_data"};
        StringBuilder command = new StringBuilder();
        String[] result;
        if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            result = new String[]{"cmd","/C", ""};
            command.append(".\\train_ner.exe");
        } else {
            result = new String[]{""};
            command.append("./train_ner");
        }
        try {
            InputStream configFileStream = NameTagServices.class.getResource("/NametagLearningConfiguration.properties").openStream();
            Properties p = new Properties();
            p.load(configFileStream);
            configFileStream.close();
            for (int i = 0; i < configNames.length; ++i) {
                try {
                    String value = (String)p.get(configNames[i]);
                    if ( value != null) {
                        configValues[i] = value;
                    } else {
                        LOG.warn("Config value " + configNames[i] + " wasn't set, using default value.");
                    }
                } catch (Exception e) {
                    LOG.warn("Config value " + configNames[i] + " wasn't set, using default value.", e);
                } finally {
                    command.append( configValues[i].isEmpty() ? "" : " " + configValues[i]);
                }
            }
        }
        catch (Exception e) {
            LOG.warn("Config file for NameTag wasn't found, using default values.", e);
            for (int i = 0; i < configNames.length; ++i) {
                command.append(configValues[i].length() > 0 ? " " + configValues[i] : "");
            }
        }

        // learning data INPUT
        command.append(" <cnec2.0-all" + File.separator + "train.txt");
        // model file OUTPUT
        command.append(" >" + outputFilePath);
        result[result.length - 1] = command.toString();
        return result;
    }

    /**
     * Learn new model
     * @param waitForModel true when learning is tu be blocking, else false
     */
    public void learn(boolean waitForModel) { //TODO: add visibility modifier
        LOG.info("Started training new nametag model");
        try {
            Runtime rt = Runtime.getRuntime();
            File dir = new File(Paths.get("../../Lingustics/training").toAbsolutePath().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SSS");
            File modelLocation = new File("models" + File.separator + "model" + sdf.format(Calendar.getInstance().getTime()) + ".ner").getAbsoluteFile();
            LOG.debug("New model path: " + modelLocation);

            String[] learningCommand = prepareLearningArguments(modelLocation);
            LOG.debug("Executing learning command: " + learningCommand[learningCommand.length - 1]);
            Process ps = rt.exec(learningCommand, null, dir);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
            String lineErr;
            String linePrev = null;
            while ((lineErr = bufferedReader.readLine()) != null) {
                linePrev = lineErr;
                //LOG.info(lineerr);  logging is useless, whole output is available after learning
            }

            boolean correctRun = true;
            if (waitForModel) {
                LOG.info("Waiting for training process");
                correctRun = ps.waitFor(5, TimeUnit.MINUTES);
            }

            if ((correctRun) && ((linePrev != null) && (linePrev.endsWith("Recognizer saved.")))) {
                LOG.info("Training done");
                this.bindModel(modelLocation);
            }
            else {
                LOG.error("Training failed: " + linePrev);
            }

        } catch (IOException e) {
            LOG.error("Training failed", e);
        } catch (InterruptedException e) {
            LOG.error("Training interrupted", e);
        }
    }

    /**
     * Translate entity type from string to Object Type
     * @param entityType entity type decoded by nametag
     * @return translated entity type
     */
    ObjectType translateEntity(String entityType) {
        ObjectType value = new ObjectType(-1L, "");
        Long id = -1L;
        try {
            id = Long.parseLong(entityType);
        }
        catch (NumberFormatException nfe) {
            // log outside of method
        }
        if (id == -1L) {
            return null;
        }
        if (idTempTable.containsKey(id)) {
            value = idTempTable.get(id);
            LOG.debug("Using CACHED entity " + value.getName());
        } else {
            try {
                ObjectTypeTable tableObject = objectTypeTableDAO.find(id);
                if (tableObject != null) {
                    value = new ObjectType(tableObject.getId(), tableObject.getName());
                    idTempTable.put(id, value);
                    LOG.debug("Using DATABASE entity " + value.getName());
                } else {
                    LOG.warn("Entity type " + entityType + " recognized, but is not stored in database.");
                }
            } catch (Exception ex) {
                LOG.warn("Exceptin occured when trying translate entity.", ex.getMessage());
            }
        }
        return value;
    }

    public List<Entity> tagText(String input)
    {
        LOG.debug(input);
        if (ner == null) {
            LOG.error("NameTag hasn't model!");
            return new ArrayList<Entity>();
        }
        Forms forms = new Forms();
        TokenRanges tokens = new TokenRanges();
        NamedEntities entities = new NamedEntities();
        ArrayList<NamedEntity> sortedEntities = new ArrayList<>();
        Scanner reader = new Scanner(input);
        List<Entity> entitiesList = new ArrayList<>();
        Stack<NamedEntity> openEntities = new Stack<>();
        Tokenizer tokenizer = ner.newTokenizer();
        boolean not_eof = true;
        while(not_eof)
        {
            StringBuilder textBuilder = new StringBuilder();
            String line;

            // Read block
            while (not_eof = reader.hasNextLine()) {
                line = reader.nextLine();
                textBuilder.append(line);
                textBuilder.append('\n');
            }
            if (not_eof) textBuilder.append('\n');

            // Tokenize and recognize
            String text = textBuilder.toString();
            tokenizer.setText(text);
            int unprinted = 0;
            while (tokenizer.nextSentence(forms, tokens)) {
                ner.recognize(forms, entities);
                sortEntities(entities, sortedEntities);

                for (int i = 0, e = 0; i < tokens.size(); i++) {
                    TokenRange token = tokens.get(i);

                    for (; e < sortedEntities.size() && sortedEntities.get(e).getStart() == i; e++) {
                        openEntities.push(sortedEntities.get(e));
                    }

                    while (!openEntities.empty() && (openEntities.peek().getStart() + openEntities.peek().getLength() - 1) == i) {
                        NamedEntity endingEntity = openEntities.peek();
                        int entity_start = (int) tokens.get((int) (i - endingEntity.getLength() + 1)).getStart();
                        int entity_end = (int) (tokens.get(i).getStart() + tokens.get(i).getLength());
                        if (openEntities.size() == 1) {
                            ObjectType recognized_entity = translateEntity(endingEntity.getType());
                            if (recognized_entity != null) {
                                LOG.warn("Recognized entity: " + encodeEntities(text.substring(entity_start, entity_end)));
                                entitiesList.add(new Entity(encodeEntities(text.substring(entity_start, entity_end)), entity_start, entity_end - entity_start - 1, recognized_entity));
                            }
                            else {
                                LOG.warn("Type " + endingEntity.getType() + " of entity " + encodeEntities(text.substring(entity_start, entity_end)) + " recognized by NameTag, but is not in database.");
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
}

class ModelsComparator implements Comparator<File> {
    @Override
    public int compare(File a, File b) {
        return a.lastModified() < b.lastModified() ? 1 : a.lastModified() > b.lastModified() ? -1 : 0;
    }
}