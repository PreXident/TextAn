package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Command for cleaning the database.
 */
@Parameters(commandDescription = "Cleans the database")
public class CleanDB extends Command {

    @Override
    public void accept(final Setuper setuper) {
        setuper.cleanDB(this);
    }
}
