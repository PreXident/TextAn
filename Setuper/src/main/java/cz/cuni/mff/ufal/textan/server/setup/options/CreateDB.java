package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Command creating the database (only for MySQL).
 */
@Parameters(commandDescription = "Creates the 'textan' database (only for MySQL).")
public class CreateDB extends Command {

    /** Path to a default server property file (inside jar). */
    private static final String DEFAULT_DATA_PROPERTIES = "data-default.properties";

    public final Properties defaults;

    @Parameter(
            description = "Creates (or overwrites) the database settings for TextAn server (data.properties) with values for the newly created database.",
            names = { "-c", "/C", "--create"})
    public boolean create = false;

    @Parameter(
            description = "Sets a username for a new database user with rights only for the 'textan' server database.",
            names = { "-username", "/username"})
    public String username;

    @Parameter(
            description = "Sets a password for a new database user with rights only for the 'textan' server database.",
            names = { "-password", "/password"})
    public String password;

    /**
     * Creates {@link cz.cuni.mff.ufal.textan.server.setup.options.CreateDB} with default data from {@link cz.cuni.mff.ufal.textan.server.setup.options.CreateDB#DEFAULT_DATA_PROPERTIES}.
     */
    public CreateDB() {
        defaults = new Properties();
        try {
            defaults.load(getClass().getClassLoader().getResourceAsStream(DEFAULT_DATA_PROPERTIES));
            username = defaults.getProperty("jdbc.user");
            password = defaults.getProperty("jdbc.pass");
        } catch (IOException e) {
            //Silent catch
        }
    }

    @Override
    public void accept(final Setuper setuper) throws IOException, SQLException {
        setuper.createDB(this);
    }
}
