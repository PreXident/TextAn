package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Main options for the Setuper.
 */
public class Options {

    /** Path to a default server property file (inside jar). */
    private static final String DEFAULT_DATA_PROPERTIES = "data-default.properties";
    /** Path to an user server property file. The file should be relative to working directory. */
    private static final String USER_DATA_PROPERTIES = "data.properties";

    /** Flag indicating whether help needs printing. */
    @Parameter(
            description = "Prints help and exits.",
            help = true,
            names = { "-h", "/H", "--help", "/?" })
    public boolean help = false;

    /** Database driver. */
    @Parameter(
            description = "Database driver, overwrites the one from database settings. Note, that there is only the MySql driver by default, other drivers must be added into the classpath.",
            names = { "-d", "/D", "--driver" })
    public String driver = null;

    /** Database user. */
    @Parameter(
            description = "Database user name, overwrites the one from database settings.",
            names = { "-n", "/N", "--user-name" })
    public String user = null;

    /** Password to the database. */
    @Parameter(
            description = "Database password, overwrites the one from database settings.",
            names = { "-p", "/P", "--password" })
    public String password = null;

    /** Path to file containing settings. */
//    @Parameter(
//            description = "file containing database settings",
//            names = { "-s", "/S", "--settings" })
//    public String settings = "data.properties";

    /** Database url. */
    @Parameter(
            description = "Database url, overwrites the one from database settings.",
            names = { "-u", "/U", "--url" })
    public String url = null;

    /** Map of commands. */
    protected Map<String, Command> commands = new TreeMap<>();

    /**
     * Only constructor.
     */
    public Options() {
        commands.put("clean_db", new CleanDB());
        commands.put("create_db", new CreateDB());
        commands.put("load_object_types", new LoadObjectTypes());
        commands.put("load_relation_types", new LoadRelationTypes());
        commands.put("list_types", new ListTypes());
        commands.put("rename_objects", new RenameObjectTypes());
        commands.put("rename_relations", new RenameRelationTypes());
        commands.put("prepare_training", new PrepareTrainingData());
    }

    /**
     * Returns command registered for the name.
     * @param name command name
     * @return command registered for the name
     */
    public Command getCommand(final String name) {
        return commands.get(name);
    }

    /**
     * Creates new JCommander for this options.
     * @return new JCommander for this options
     */
    public JCommander createJCommander() {
        final JCommander jCommander = new JCommander(this);
        for (Entry<String, Command> entry : commands.entrySet()) {
            jCommander.addCommand(entry.getKey(), entry.getValue());
        }
        jCommander.setProgramName("java -cp TextAn-server.jar cz.cuni.mff.ufal.textan.setup.Setuper"); //set name in usage
        return jCommander;
    }

    /**
     * Processes settings if needed and fills apropriate fields if needed.
     */
    public void processSettings() {
        if (driver != null
                && user != null
                && password != null
                && url != null) {
            return;
        }

        try {
            //load default properties from jar
            //DEFAULT_DATA_PROPERTIES must be in classpath
            Properties defaults = new Properties();
            defaults.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_DATA_PROPERTIES));

            //load user properties
            Properties properties = new Properties(defaults);
            File userPropertiesFile = new File(USER_DATA_PROPERTIES);
            if (userPropertiesFile.exists()) {
                try (InputStream stream = new FileInputStream(userPropertiesFile)) {
                    properties.load(stream);
                }
            }

            if (driver == null) {
                driver = properties.getProperty("jdbc.driverClassName");
            }
            if (user == null) {
                user = properties.getProperty("jdbc.user");
            }
            if (password == null) {
                password = properties.getProperty("jdbc.pass");
            }
            if (url == null) {
                url = properties.getProperty("jdbc.url");
            }

        } catch (Exception e) {
            System.err.printf("Error while reading settings from file \"%s\"\n", e.getMessage());
            e.printStackTrace();
        }
    }
}
