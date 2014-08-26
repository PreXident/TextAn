package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.FileReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Main options for the Setuper.
 */
public class Options {

    /** Database driver. */
    @Parameter(
            description = "database driver, overwrites the one from database settings",
            names = { "-d", "/D", "--driver" })
    public String driver = null;

    /** Flag indicating whether help needs printing. */
    @Parameter(
            description = "prints help and exits",
            help = true,
            names = { "-h", "/H", "--help", "/?" })
    public boolean help = false;

    /** Database user. */
    @Parameter(
            description = "database user name, overwrites the one from database settings",
            names = { "-n", "/N", "--user-name" })
    public String user = null;

    /** Password to the database. */
    @Parameter(
            description = "database password, overwrites the one from database settings",
            names = { "-p", "/P", "--password" })
    public String password = null;

    /** Path to file containing settings. */
    @Parameter(
            description = "file containing database settings",
            names = { "-s", "/S", "--settings" })
    public String settings = "data.properties";

    /** Database url. */
    @Parameter(
            description = "database url, overwrites the one from database settings",
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
        commands.put("load_objects", new LoadObjectTypes());
        commands.put("load_relations", new LoadRelationTypes());
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
        jCommander.setProgramName("java -jar Server-setup.jar"); //set name in usage
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
        final Properties properties = new Properties();
        try (FileReader reader = new FileReader(settings)) {
            properties.load(reader);
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
            System.err.printf("Error while reading settings from file \"%s\"\n", settings);
            e.printStackTrace();
        }
    }
}
