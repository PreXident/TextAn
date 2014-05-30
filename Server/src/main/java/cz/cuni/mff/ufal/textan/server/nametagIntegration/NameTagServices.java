package cz.cuni.mff.ufal.textan.server.nametagIntegration;

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
    }

    public void bindModel(String pathToModel) {
        ner = Ner.load(pathToModel);
        if (ner == null) {
            LOG.error("Model wasn't found!"); //TODO: throw some exception, what it returns if model exists, bud...
        }
        idTempTable = new Hashtable<>();
    }

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

    /**
     * Function that creates commands for learning new nametag model.
     * @return string array with commands
     */
    String[] prepareLearningArguments() {
        String[] configValues = {"czech", "morphodita:czech-131112-pos_only.tagger", "features-tsd13.txt", "2","30", "-0.1", "0.1", "0.01", "0.5", "0", ""};
        String[] configNames = {"ner_identifier", "tagger", "featuresFile", "stages", "iterations", "missing_weight", "initial_learning_rage", "final_learning_rage", "gaussian", "hidden_layer", "heldout_data"};
        StringBuilder command = new StringBuilder();
        String[] result;
        // binary and setting splitter
        String pathSplitter;
        if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            result = new String[]{"cmd","/C", ""};
            command.append(".\\train_ner.exe");
            pathSplitter = "\\";
        } else {
            result = new String[]{""};
            command.append("./train_ner");
            pathSplitter = "/";
        }
        try {
            InputStream configFileStream = NameTagServices.class.getResource("/NametagLearningConfiguration.cnf").openStream();
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

        /*
        // taggger
        result.append(" czech morphodita:czech-131112-pos_only.tagger");
        // features
        result.append(" features-tsd13.txt");
        // training parameters
        result.append(" 2 30 -0.1 0.1 0.01 0.5 0");
        // test file
        result.append(" cnec2.0-all" + pathSplitter + "dtest.txt"); */
        // learning data INPUT
        command.append(" <cnec2.0-all" + pathSplitter + "train.txt");
        // model file OUTPUT
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SSS");
        String modelLocation = "model" + sdf.format(Calendar.getInstance().getTime()) + ".ner";
        command.append(" >." + pathSplitter + modelLocation);
        result[result.length - 1] = command.toString();
        return result;
    }

    /**
     * Learn new model
     */
    void learn() { //TODO: add visibility modifier
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        LOG.info("Started training at " + sdf.format(cal.getTime()));
        try {
            Runtime rt = Runtime.getRuntime();
            File dir = new File(Paths.get("../NameTagIntegration/training").toAbsolutePath().toString());
            Process ps;
            //String modelPath;
            ps = rt.exec(prepareLearningArguments(), null, dir);

            BufferedReader bes = new BufferedReader(new InputStreamReader(ps.getErrorStream())); //Dont't know why, but output is in error stream
            String lineerr;
            while ((lineerr = bes.readLine()) != null) {
                LOG.info(lineerr);
            }
        } catch (IOException e) {
            LOG.error("Training failed " + sdf.format(cal.getTime()), e);
        }

        LOG.info("Training done at " + sdf.format(cal.getTime()));
        LOG.info("Changing ner.");
        this.bindModel("../../NameTagIntegration/training/czech-140205-cnec2.0.ner");
        LOG.info("Ner changed.");
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
            while ((not_eof = reader.hasNextLine()) && !(line = reader.nextLine()).isEmpty()) {
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