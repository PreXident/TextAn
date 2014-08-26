package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Command for loading object types to the database.
 */
@Parameters(commandDescription = "Loads object types to the database")
public class LoadObjectTypes extends LoadTypes {

    @Override
    public void accept(final Setuper setuper) {
        prepareTypes();
        setuper.createObjectTypes(this);
    }
}
