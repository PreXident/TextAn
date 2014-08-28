package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.sql.SQLException;

/**
 * Command for renaming object types in the database.
 */
@Parameters(commandDescription = "Renames object types in the database")
public class RenameObjectTypes extends RenameTypes {

    @Override
    public void accept(final Setuper setuper) throws SQLException {
        prepareMapping();
        setuper.renameObjectTypes(this);
    }
}
