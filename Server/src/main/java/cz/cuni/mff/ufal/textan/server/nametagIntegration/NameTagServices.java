package cz.cuni.mff.ufal.textan.server.nametagIntegration;

import cz.cuni.mff.ufal.nametag.*;
import cz.cuni.mff.ufal.textan.server.models.Entity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jakub Vlcek on 29. 4. 2014.
 */
public class NameTagServices {
    private Ner ner;
    private static final Logger LOG = LoggerFactory.getLogger(NameTagServices.class);
    Hashtable<String, Long> translationTable;

    public NameTagServices(String model) {
        ner = Ner.load(model);
        if (ner == null) {
            LOG.error("Model wasn't found!");
        }
        translationTable = new Hashtable<>();
        translationTable.put("P", 1L); //osoba
        translationTable.put("PS", 1L); //osoba
        translationTable.put("PF", 1L); //osoba
        translationTable.put("TD", 2L); //datum
        translationTable.put("GS", 3L); //ulice
        translationTable.put("GC", 4L); //mesto
        translationTable.put("GQ", 4L); //mestska cast
        //translationTable.put("", 5L); //zbran
        translationTable.put("TH", 6L); //cas
        //translationTable.put("", 7L); //automobil
        //translationTable.put("", 8L); //SPZ
        //translationTable.put("", 9L); //Podnik
        //translationTable.put("", 10L); //Zakon

    }

    long translateEntity(String entityType) {
        long value = 0L;
        if (translationTable.containsKey(entityType.toUpperCase())) {
            value = translationTable.get(entityType.toUpperCase());
        } else {
            try {
                value = Long.parseLong(entityType);
            } catch (NumberFormatException nfe) {
                LOG.warn("Entity type " + entityType + " wasn't recognized.", nfe);
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
        command.append(" >." + pathSplitter + "model" + sdf.format(Calendar.getInstance().getTime()) + ".ner");
        result[result.length - 1] = command.toString();
        return result;
    }

    /**
     * Learn new model
     */
    void Learn() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        LOG.info("Started training at " + sdf.format(cal.getTime()));
        try {
            Runtime rt = Runtime.getRuntime();
            File dir = new File(Paths.get("../NameTagIntegration/training").toAbsolutePath().toString());
            Process ps;
            ps = rt.exec(prepareLearningArguments(), null, dir);

            /*BufferedReader bes = new BufferedReader(new InputStreamReader(ps.getErrorStream())); //Dont't know why, but output is in error stream
            String lineerr;
            while ((lineerr = bes.readLine()) != null) {
                LOG.info(lineerr);
            }*/
        } catch (IOException e) {
            LOG.error("Training failed " + sdf.format(cal.getTime()), e);
        }

        LOG.info("Training done at " + sdf.format(cal.getTime()));
        LOG.info("Changing ner.");
        Ner tmpNer = Ner.load("../../NameTagIntegration/training/czech-140205-cnec2.0.ner");
        ner = tmpNer;
        LOG.info("Ner changed.");
    }


    public List<Entity> TagText(String input)
    {
        if (ner == null) {
            LOG.error("NameTag wasn't initialized!");
            return new ArrayList<Entity>();
        }
        Tokenizer tokenizer = ner.newTokenizer();
        Forms forms = new Forms();
        TokenRanges tokens = new TokenRanges();
        NamedEntities entities = new NamedEntities();
        ArrayList<NamedEntity> sortedEntities = new ArrayList<>();
        Scanner reader = new Scanner(input);
        List<Entity> entitiesList = new ArrayList<>();
        Stack<NamedEntity> openEntities = new Stack<>();
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

                for (int i = 0, e = 0; i < entities.size(); i++) {
                    TokenRange token = tokens.get(i);
                    //int token_start = (int) token.getStart();
                    //int token_end = (int) token.getStart() + (int) token.getLength();
                    //if (unprinted < token_start) System.out.print(encodeEntities(text.substring(unprinted, token_start)));

                    for (; e < sortedEntities.size() && sortedEntities.get(e).getStart() == i; e++) {
                        String ent = sortedEntities.get(e).getType();
                        System.out.printf("<ne type=\"%s\">", ent);
                        openEntities.push(sortedEntities.get(e));
                    }
                    // pridat zjisteni id entity
                    //Entity ent = new Entity(text.substring(token_start, token_end), token_start, token_end, 0);
                    //entitiesList.add(ent);

                    while (!openEntities.empty() && (openEntities.peek().getStart() + openEntities.peek().getLength() - 1) == i) {
                        NamedEntity endingEntity = openEntities.peek();
                        int entity_start = (int) tokens.get((int) (i - endingEntity.getLength() + 1)).getStart();
                        int entity_end = (int) (tokens.get(i).getStart() + tokens.get(i).getLength());
                        if (openEntities.size() == 1) {
                            //entitiesList.add(new Entity(encodeEntities(text.substring(entity_start, entity_end)), (int)endingEntity.getStart() + 1, i - (int)endingEntity.getStart() + 2, translateEntity(endingEntity.getType())));
                            entitiesList.add(new Entity(encodeEntities(text.substring(entity_start, entity_end)), entity_start, entity_end - entity_start - 1, translateEntity(endingEntity.getType())));
                            LOG.error(entity_start + ":" + (entity_end - entity_start) + "-" + translateEntity(endingEntity.getType()));
                        }
                        openEntities.pop();
                    }

                        /*
                        // Close entities that end sooned than current entity
                        while (!openEntities.empty() && openEntities.peek() < entity_start) {
                            if (unprinted < openEntities.peek()) System.out.print(encodeEntities(text.substring(unprinted, openEntities.peek())));
                            unprinted = openEntities.pop();
                            System.out.print("</ne>");
                        }

                        // Print text just before the entity, open it and add end to the stack
                        if (unprinted < entity_start) System.out.print(encodeEntities(text.substring(unprinted, entity_start)));
                        unprinted = entity_start;
                        System.out.printf("<ne type=\"%s\">", entity.getType());
                        openEntities.push(entity_end);
                        */
                }
        /*
                    // Close unclosed entities
                    while (!openEntities.empty()) {
                        if (unprinted < openEntities.peek()) System.out.print(encodeEntities(text.substring(unprinted, openEntities.peek())));
                        unprinted = openEntities.pop();
                        System.out.print("</ne>");
                    }
                    */
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
