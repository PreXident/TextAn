package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Command for listing object and relation types in the database.
 */
@Parameters(commandDescription = "Lists object and relation types in the database")
public class ListTypes extends Command {

    @Override
    public void accept(final Setuper setuper) {
        setuper.listTypes(this);
    }
}
