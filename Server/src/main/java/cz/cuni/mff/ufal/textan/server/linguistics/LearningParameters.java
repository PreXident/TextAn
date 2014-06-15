package cz.cuni.mff.ufal.textan.server.linguistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Vlcak on 15.6.2014.
 */

/**
 * Class for read nametag training parameters.
 */
public class LearningParameters {
    private static final String TRAIN_NER = "train_ner";
    private static final String WAITING_TIME = "waiting_time";
    private static final String TRAINING_DATA = "training_data";
    private static final String DEFAULT_TRAINING_DATA_PATH = "cnec2.0-all" + File.separator + "train.txt";
    private static final Long DEFAULT_WAITING_TIME = 300000L;
    private static final String DEFAULT_NER_IDENTIFIER = "czech";
    private static final String DEFAULT_TAGGER = "morphodita:czech-131112-pos_only.tagger";
    private static final String DEFAULT_FEATURES_FILE = "features-tsd13.txt";
    private static final String DEFAULT_STAGES = "2";
    private static final String DEFAULT_ITERATIONS = "30";
    private static final String DEFAULT_MISSING_WEIGHT = "-0.1";
    private static final String DEFAULT_INITIAL_LEARNING_RATE = "0.1";
    private static final String DEFAULT_FINAL_LEARNING_RATE = "0.01";
    private static final String DEFAULT_GAUSSIAN = "0.5";
    private static final String DEFAULT_HIDDEN_LAYER = "0";
    private static final String DEFAULT_HELDOUT_DATA = "";

    private static final Logger LOG = LoggerFactory.getLogger(LearningParameters.class);

    private String[] configValues = { DEFAULT_NER_IDENTIFIER,
                                      DEFAULT_TAGGER,
                                      DEFAULT_FEATURES_FILE,
                                      DEFAULT_STAGES,
                                      DEFAULT_ITERATIONS,
                                      DEFAULT_MISSING_WEIGHT,
                                      DEFAULT_INITIAL_LEARNING_RATE,
                                      DEFAULT_FINAL_LEARNING_RATE,
                                      DEFAULT_GAUSSIAN,
                                      DEFAULT_HIDDEN_LAYER,
                                      DEFAULT_HELDOUT_DATA };
    private String[] configNames = {"ner_identifier", "tagger", "featuresFile", "stages", "iterations", "missing_weight", "initial_learning_rage", "final_learning_rage", "gaussian", "hidden_layer", "heldout_data"};

    private List<String> command;

    private Long waitingTime;
    private File trainingDataFile;

    public LearningParameters(File workingDirectory) {
        command = new LinkedList<>();
        command.add(new File(workingDirectory, mapBinaryName(TRAIN_NER)).toString());


        try (InputStream configFileStream = NamedEntityRecognizer.class.getResource("/NametagLearning.properties").openStream()){
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
                        command.add(configValues[i]);
                    }
                }
            }

            try {
                String value = (String)p.get(TRAINING_DATA);
                if (value != null) {
                    if (new File(workingDirectory.toString() + File.separator + value).exists()) {
                        trainingDataFile = new File(workingDirectory + File.separator + value).getCanonicalFile();
                    }
                    else {
                        LOG.warn("Training data file {} not exists, using default value.", TRAINING_DATA);
                    }
                } else {
                    LOG.warn("Config value {} wasn't set, using default value.", TRAINING_DATA);
                }
            }
            catch (Exception e) {
                LOG.warn("Config value {} wasn't set, using default value.", TRAINING_DATA);
            }
            finally {
                if (trainingDataFile == null) {
                    try {
                        trainingDataFile = new File(workingDirectory.getAbsolutePath() + File.separator + DEFAULT_TRAINING_DATA_PATH).getCanonicalFile();
                    } catch (IOException e) {
                        LOG.error("Default learning data file not exists.", e);
                    }
                }
            }

            try {
                Long valueL  = Long.parseLong((String)p.get(WAITING_TIME));
                if (valueL != null) {
                    waitingTime = valueL;
                } else {
                    waitingTime = DEFAULT_WAITING_TIME;
                    LOG.warn("Config value {} wasn't set, using default value.", WAITING_TIME);
                }
            }
            catch (NumberFormatException nfe) {
                LOG.warn("Could not parse config value {}, using default value.", WAITING_TIME);
            }
            catch (Exception e) {
                LOG.warn("Config value {} wasn't set, using default value.", WAITING_TIME);
            }

        } catch (Exception e) {
            LOG.warn("Config file for NameTag wasn't found, using default values.", e);
            for (int i = 0; i < configNames.length; ++i) {
                if (!configValues[i].isEmpty()) {
                    command.add(configValues[i]);
                }
            }
        }
    }

    /**
     * @return list of command line arguments
     */
    public List<String> getCommand() {
        return command;
    }

    /**
     * @return maximum waiting time for learning
     */
    public Long getWaitingTime() {
        return waitingTime;
    }

    /**
     * @return file with training data
     */
    public File getTrainingData() {
        return trainingDataFile;
    }

    private static String mapBinaryName(String binName) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return binName + ".exe";
        } else {
            return binName;
        }
    }



}
