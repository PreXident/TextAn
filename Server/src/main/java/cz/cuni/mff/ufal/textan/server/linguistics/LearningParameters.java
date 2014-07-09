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
    private static final String TRAINING_DATA = "default_training_data_file";
    private static final String USE_DEFAULT_TRAINING_DATA = "use_default_training_data";
    private static final String MAXIMUM_STORED_MODELS = "maximum_stored_models";
    private static final String DEFAULT_TRAINING_DATA_PATH = "cnec2.0-all" + File.separator + "train.txt";
    private static final int DEFAULT_WAITING_TIME = 300000;
    private static final int DEFAULT_MAXIMUM_STORED_MODELS = 5;
    private static final boolean DEFAULT_USE_DEFAULT_DATA = true;
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

    private static final String[] DEFAULT_CONFIG_VALUES = { DEFAULT_NER_IDENTIFIER,
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
    private static final String[] LEARNING_PARAM_NAMES = {"ner_identifier", "tagger", "featuresFile", "stages", "iterations", "missing_weight", "initial_learning_rage", "final_learning_rage", "gaussian", "hidden_layer", "heldout_data"};

    private List<String> command;

    private int waitingTime;
    private int maximumStoredModels;
    private File trainingDataFile;

    private boolean useDefaultTrainingData;

    /**
     * Read learning parameters for nametag
     * @param binaryDirectory directory, where learning binary is stored
     * @param workingDirectory working directory, where learning files are stored
     * */
    public LearningParameters(File binaryDirectory, File workingDirectory) {
        command = new LinkedList<>();
        command.add(new File(binaryDirectory, mapBinaryName(TRAIN_NER)).toString());


        try (InputStream configFileStream = NamedEntityRecognizer.class.getResource("/NametagLearning.properties").openStream()){
            Properties p = new Properties();
            p.load(configFileStream);
            String value;
            for (int i = 0; i < LEARNING_PARAM_NAMES.length; ++i) {
                value = getStringProperty(p, LEARNING_PARAM_NAMES[i], DEFAULT_CONFIG_VALUES[i]);
                if (!value.isEmpty()) {
                    command.add(value);
                }
            }

            useDefaultTrainingData = getBooleanProperty(p, USE_DEFAULT_TRAINING_DATA, DEFAULT_USE_DEFAULT_DATA);

            if (useDefaultTrainingData) {
                try {
                    trainingDataFile = new File(workingDirectory.getAbsolutePath() + File.separator + getStringProperty(p, TRAINING_DATA, DEFAULT_TRAINING_DATA_PATH)).getCanonicalFile();
                } catch (IOException e) {
                    LOG.warn("Config value {} wasn't set, using default value.", TRAINING_DATA);
                } finally {
                    if (trainingDataFile == null) {
                        try {
                            trainingDataFile = new File(workingDirectory.getAbsolutePath() + File.separator + DEFAULT_TRAINING_DATA_PATH).getCanonicalFile();
                        } catch (IOException e) {
                            LOG.error("Default learning data file not exists.", e);
                        }
                    }
                }
            }

            waitingTime = getIntegerProperty(p, WAITING_TIME, DEFAULT_WAITING_TIME);

            maximumStoredModels = getIntegerProperty(p, MAXIMUM_STORED_MODELS, DEFAULT_MAXIMUM_STORED_MODELS);



        } catch (Exception e) {
            LOG.warn("Config file for NameTag wasn't found, using default values.", e.getMessage());
            for (int i = 0; i < LEARNING_PARAM_NAMES.length; ++i) {
                if (!DEFAULT_CONFIG_VALUES[i].isEmpty()) {
                    command.add(DEFAULT_CONFIG_VALUES[i]);
                }
            }

            try {
                trainingDataFile = new File(workingDirectory.getAbsolutePath() + File.separator + DEFAULT_TRAINING_DATA_PATH).getCanonicalFile();
            }
            catch (IOException ioe) {
                LOG.warn("Config value {} wasn't set, using default value.", TRAINING_DATA);
            } finally {
                if (trainingDataFile == null) {
                    try {
                        trainingDataFile = new File(workingDirectory.getAbsolutePath() + File.separator + DEFAULT_TRAINING_DATA_PATH).getCanonicalFile();
                    } catch (IOException ioe) {
                        LOG.error("Default learning data file not exists.", ioe);
                    }
                }
            }

            waitingTime = DEFAULT_WAITING_TIME;
            maximumStoredModels = DEFAULT_MAXIMUM_STORED_MODELS;
        }
    }

    /**
     * Return string property from provided properties
     * @param p source properties
     * @param propertyName property name
     * @param defaultValue default value
     * @return property value if property is set, default value else
     */
    private String getStringProperty(Properties p, String propertyName, String defaultValue) {
        String value = defaultValue;
        try {
            value = (String) p.get(propertyName);
            if (value == null) {
                LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
                value = defaultValue;
            }
        } catch (Exception e) {
            LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
        }
        return value;
    }

    /**
     * Return boolean property from provided properties
     * @param p source properties
     * @param propertyName property name
     * @param defaultValue default value
     * @return property value if property is set, default value else
     */
    private boolean getBooleanProperty(Properties p, String propertyName, boolean defaultValue) {
        Boolean value = defaultValue;
        try {
            value = Boolean.valueOf((String) p.get(propertyName));
            if (value == null) {
                LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
                value = defaultValue;
            }
        } catch (Exception e) {
            LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
        }
        return value;
    }

    /**
     * Return integer property from provided properties
     * @param p source properties
     * @param propertyName property name
     * @param defaultValue default value
     * @return property value if property is set, default value else
     */
    private int getIntegerProperty(Properties p, String propertyName, int defaultValue) {
        Integer value = defaultValue;
        try {
            value  = Integer.parseInt((String)p.get(propertyName));
            if (value == null) {
                value = defaultValue;
                LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
            }
        }
        catch (NumberFormatException nfe) {
            LOG.warn("Could not parse config value {}, using default value {}.", propertyName, defaultValue);
        }
        catch (Exception e) {
            LOG.warn("Config value {} wasn't set, using default value {}.", propertyName, defaultValue);
        }
        return value;
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
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * @return maximum stored models on disk
     */
    public int getMaximumStoredModels() {
        return maximumStoredModels;
    }

    /**
     * @return true if default training data should be used
     */
    public boolean useDefaultTrainingData() {
        return useDefaultTrainingData;
    }

    /**
     * @return file with training data
     */
    public File getTrainingData() {
        return trainingDataFile;
    }

    /**
     * map binary name based on OS
     * @param binName input binary name
     * @return mapped binary name
     */
    private static String mapBinaryName(String binName) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return binName + ".exe";
        } else {
            return binName;
        }
    }



}
