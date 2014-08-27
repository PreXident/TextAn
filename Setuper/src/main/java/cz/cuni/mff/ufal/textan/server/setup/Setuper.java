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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple class for batch report processing.
 * For executing different commands uses visitor-like pattern.
 */
public class Setuper {

    private static final int EXIT_STATUS_HELP = 1;
    private static final int EXIT_STATUS_MISSING_PARAM = 2;
    private static final int EXIT_STATUS_MISSING_CLASS= 3;
    private static final int EXIT_STATUS_SQL_PROBLEM= 3;


    /**
     * Main method.
     * @param args command line arguments
     */
    public static void main(final String[] args) {

        try {
            //parse command line options
            final Options options = new Options();
            final JCommander jCommander = options.createJCommander();
            jCommander.parse(args);
            if (options.help || jCommander.getParsedCommand() == null) {
                jCommander.usage();
                System.exit(EXIT_STATUS_HELP);
            }
            options.processSettings();
            if (options.driver == null) {
                System.out.println("Database driver not specified!");
                jCommander.usage();
                System.exit(EXIT_STATUS_MISSING_PARAM);
            }
            if (options.url == null) {
                System.out.println("Database url not specified!");
                jCommander.usage();
                System.exit(EXIT_STATUS_MISSING_PARAM);
            }
            if (options.user == null) {
                System.out.println("Database user not specified!");
                jCommander.usage();
                System.exit(EXIT_STATUS_MISSING_PARAM);
            }
            if (options.password == null) {
                System.out.println("Database password not specified!");
                jCommander.usage();
                System.exit(EXIT_STATUS_MISSING_PARAM);
            }
            //
            final String commandName = jCommander.getParsedCommand();
            final Command command = options.getCommand(commandName);
            new Setuper(options).execute(command);

        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_STATUS_MISSING_CLASS);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_STATUS_SQL_PROBLEM);
        }
    }

    /** Application options. */
    protected final Options options;

    /** Database connection */
    protected Connection connection;

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
    public void execute(final Command command) throws ClassNotFoundException, SQLException {

        //Open db connection
        try {
            Class.forName(options.driver);
        } catch (ClassNotFoundException e) {
            System.err.println("The JDBC driver '{}' was not found.");
            throw e;
        }

        connection = null;
        try {
            connection = DriverManager.getConnection(options.url, options.user, options.password);
        } catch (SQLException e) {
            System.err.println("Connection to database failed.");
            throw e;
        }

        try {
            command.accept(this);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Cleans the database.
     * @param command command options
     */
    public void cleanDB(final CleanDB command) {

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
