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
import cz.cuni.mff.ufal.textan.server.setup.utils.ScriptRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Simple class for batch report processing.
 * For executing different commands uses double dispatch.
 */
public class Setuper {

    private static final int EXIT_STATUS_HELP = 1;
    private static final int EXIT_STATUS_MISSING_PARAM = 2;
    private static final int EXIT_STATUS_MISSING_CLASS = 3;
    private static final int EXIT_STATUS_SQL_PROBLEM = 3;
    private static final int EXIT_STATUS_IO_PROBLEM = 4;
    private static final int EXIT_STATUS_UNEXPECTED_PROBLEM = 100;

    private static final String CREATE_SCRIPT_FILENAME = "create.sql";
    private static final String CLEAN_SCRIPT_FILENAME = "clean.sql";

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
            System.err.println("ERROR: " + e.getMessage());
            System.exit(EXIT_STATUS_MISSING_CLASS);
        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(EXIT_STATUS_SQL_PROBLEM);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(EXIT_STATUS_IO_PROBLEM);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(EXIT_STATUS_UNEXPECTED_PROBLEM);
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
     * Executes command using double dispatch.
     * @param command command to execute
     */
    public void execute(final Command command) throws ClassNotFoundException, SQLException, IOException {

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
    public void cleanDB(final CleanDB command) throws IOException, SQLException {
        //TODO test if connection  != null
        try (Reader cleanScriptReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(CLEAN_SCRIPT_FILENAME))) {
            runScript(cleanScriptReader);
        }
    }

    /**
     * Creates the database.
     * @param command command options
     */
    public void createDB(final CreateDB command) throws IOException, SQLException {
        //TODO test if connection  != null
        try (Reader createScriptReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(CREATE_SCRIPT_FILENAME))) {
            runScript(createScriptReader);
        }
    }

    /**
     * Loads object types to the database.
     * @param command command options
     */
    public void createObjectTypes(final LoadObjectTypes command) throws SQLException {
        //TODO test if connection  != null
        String insertObjectTypeQuery = "INSERT INTO ObjectType (name) VALUES (?)";
        createTypes(insertObjectTypeQuery, command.types);
    }

    /**
     * Loads relation types to the database.
     * @param command command options
     */
    public void createRelationTypes(final LoadRelationTypes command) throws SQLException {
        //TODO test if connection  != null
        String insertRelationTypeQuery = "INSERT INTO RelationType (name) VALUES (?)";
        createTypes(insertRelationTypeQuery, command.types);
    }

    /**
     * Listing object and relation types in the database.
     * @param command command options
     */
    public void listTypes(final ListTypes command) throws SQLException {
        //TODO test if connection  != null

        try (Statement statement = connection.createStatement()) {
            String objectTypesQuery = "SELECT id_object_type, name FROM ObjectType ORDER BY id_object_type";
            ResultSet objectTypes = statement.executeQuery(objectTypesQuery);

            System.out.println("Object types");
            System.out.println("ID\tName");
            while (objectTypes.next()) {
                long id = objectTypes.getLong("id_object_type");
                String name = objectTypes.getString("name");
                System.out.printf("%s\t%s\n", id, name);
            }

            System.out.println();

            String relationTypesQuery = "SELECT id_relation_type, name FROM RelationType ORDER BY id_relation_type";
            ResultSet relationTypes = statement.executeQuery(relationTypesQuery);

            System.out.println("Relation types");
            System.out.println("ID\tName");
            while (relationTypes.next()) {
                long id = relationTypes.getLong("id_relation_type");
                String name = relationTypes.getString("name");
                System.out.printf("%s\t%s\n", id, name);
            }
        }
    }

    /**
     * Renames object types to the database.
     * @param command command options
     */
    public void renameObjectTypes(final RenameObjectTypes command) throws SQLException {
        if (command.id) {
            String updateObjectTypesQuery = "UPDATE ObjectType SET name=? WHERE id_object_type=?";
            renameTypesId(updateObjectTypesQuery, command.mapping);
        } else {
            String updateObjectTypesQuery = "UPDATE ObjectType SET name=? WHERE name=?";
            renameTypesName(updateObjectTypesQuery, command.mapping);
        }
    }

    /**
     * Renames relation types to the database.
     * @param command command options
     */
    public void renameRelationTypes(final RenameRelationTypes command) throws SQLException {
        if (command.id) {
            String updateObjectTypesQuery = "UPDATE RelationType SET name=? WHERE id_relation_type=?";
            renameTypesId(updateObjectTypesQuery, command.mapping);
        } else {
            String updateObjectTypesQuery = "UPDATE RelationType SET name=? WHERE name=?";
            renameTypesName(updateObjectTypesQuery, command.mapping);
        }
    }

    /**
     * Prepares training data.
     * @param command command options
     */
    public void prepareTrainingData(final PrepareTrainingData command) {
        //TODO implement
    }

    private void createTypes(String query, List<String> names) throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (String typeName : names) {
                statement.setString(1, typeName);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void renameTypesId(String query, Map<String, String> mapping) throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(query)){

            for (Map.Entry<String, String> pair : mapping.entrySet()) {
                long id = Long.parseLong(pair.getKey());
                String name = pair.getValue();

                statement.setString(1, name);
                statement.setLong(2, id);
                statement.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void renameTypesName(String query, Map<String, String> mapping) throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement statement = connection.prepareStatement(query)){

            for (Map.Entry<String, String> pair : mapping.entrySet()) {
                String oldName = pair.getKey();
                String name = pair.getValue();

                statement.setString(1, name);
                statement.setString(2, oldName);
                statement.executeUpdate();
            }

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void runScript(Reader script) throws SQLException, IOException {
        connection.setAutoCommit(false);
        try {
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
            scriptRunner.setLogWriter(null);
            scriptRunner.setErrorLogWriter(null);
            scriptRunner.runScript(script);
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
