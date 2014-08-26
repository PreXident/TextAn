package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Command for loading relation types to the database.
 */
@Parameters(commandDescription = "Loads relation types to the database")
public class LoadRelationTypes extends LoadTypes {

    @Override
    public void accept(final Setuper setuper) {
        prepareTypes();
        setuper.createRelationTypes(this);
    }
}
