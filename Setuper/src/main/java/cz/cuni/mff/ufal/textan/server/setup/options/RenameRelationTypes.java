package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.sql.SQLException;

/**
 * Command for renaming relation types in the database.
 */
@Parameters(commandDescription = "Renames relation types in the database.")
public class RenameRelationTypes extends RenameTypes {

    @Override
    public void accept(final Setuper setuper) throws SQLException {
        prepareMapping();
        setuper.renameRelationTypes(this);
    }
}
