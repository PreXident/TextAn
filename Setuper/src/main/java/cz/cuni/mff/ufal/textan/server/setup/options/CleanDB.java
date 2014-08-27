package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Command for cleaning the database.
 */
@Parameters(commandDescription = "Cleans the database")
public class CleanDB extends Command {

    @Override
    public void accept(final Setuper setuper) throws IOException, SQLException {
        setuper.cleanDB(this);
    }
}
