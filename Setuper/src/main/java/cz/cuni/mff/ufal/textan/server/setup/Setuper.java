package cz.cuni.mff.ufal.textan.server.setup;

import com.beust.jcommander.JCommander;
import cz.cuni.mff.ufal.textan.server.setup.options.CleanDB;
import cz.cuni.mff.ufal.textan.server.setup.options.Command;
import cz.cuni.mff.ufal.textan.server.setup.options.CreateDB;
import cz.cuni.mff.ufal.textan.server.setup.options.ListTypes;
import cz.cuni.mff.ufal.textan.server.setup.options.LoadObjectTypes;
import cz.cuni.mff.ufal.textan.server.setup.options.LoadRelationTypes;
import cz.cuni.mff.ufal.textan.server.setup.options.Options;
import cz.cuni.mff.ufal.textan.server.setup.options.PrepareTrainingData;
import cz.cuni.mff.ufal.textan.server.setup.options.RenameObjectTypes;
import cz.cuni.mff.ufal.textan.server.setup.options.RenameRelationTypes;

/**
 * Simple class for batch report processing.
 * For executing different commands uses visitor-like pattern.
 */
public class Setuper {

    /**
     * Main method.
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        //parse command line options
        final Options options = new Options();
        final JCommander jCommander = options.createJCommander();
        jCommander.parse(args);
        if (options.help || jCommander.getParsedCommand() == null) {
            jCommander.usage();
            return;
        }
        options.processSettings();
        if (options.driver == null) {
            System.out.println("Database driver not specified!");
            jCommander.usage();
            return;
        }
        if (options.url == null) {
            System.out.println("Database url not specified!");
            jCommander.usage();
            return;
        }
        if (options.user == null) {
            System.out.println("Database user not specified!");
            jCommander.usage();
            return;
        }
        if (options.password == null) {
            System.out.println("Database password not specified!");
            jCommander.usage();
            return;
        }
        //
        final String commandName = jCommander.getParsedCommand();
        final Command command = options.getCommand(commandName);
        new Setuper(options).execute(command);
    }

    /** Application options. */
    protected final Options options;

    /**
     * Only constructor.
     * @param options application options
     */
    public Setuper(final Options options) {
        this.options = options;
    }

    /**
     * Executes command using visitor pattern.
     * @param command command to execute
     */
    public void execute(final Command command) {
        command.accept(this);
    }

    /**
     * Cleans the database.
     * @param command command options
     */
    public void cleanDB(final CleanDB command) {
        //TODO implement
    }

    /**
     * Creates the database.
     * @param command command options
     */
    public void createDB(final CreateDB command) {
        //TODO implement
    }

    /**
     * Loads object types to the database.
     * @param command command options
     */
    public void createObjectTypes(final LoadObjectTypes command) {
        //TODO implement
    }

    /**
     * Loads relation types to the database.
     * @param command command options
     */
    public void createRelationTypes(final LoadRelationTypes command) {
        //TODO implement
    }

    /**
     * Listing object and relation types in the database.
     * @param command command options
     */
    public void listTypes(final ListTypes command) {
        //TODO implement
    }

    /**
     * Renames object types to the database.
     * @param command command options
     */
    public void renameObjectTypes(final RenameObjectTypes command) {
        //TODO implement
    }

    /**
     * Renames relation types to the database.
     * @param command command options
     */
    public void renameRelationTypes(final RenameRelationTypes command) {
        //TODO implement
    }

    /**
     * Prepares training data.
     * @param command command options
     */
    public void prepareTrainingData(final PrepareTrainingData command) {
        //TODO implement
    }
}
