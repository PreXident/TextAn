package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Command creating the database (only for MySQL).
 */
@Parameters(commandDescription = "Creates the database (only for MySQL)")
public class CreateDB extends Command {

    @Override
    public void accept(final Setuper setuper) throws IOException, SQLException {
        setuper.createDB(this);
    }
}
