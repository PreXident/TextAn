package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Command creating the database (only for MySQL).
 */
@Parameters(commandDescription = "Creates the database (only for MySQL)")
public class CreateDB extends Command {

    @Override
    public void accept(final Setuper setuper) {
        setuper.createDB(this);
    }
}
