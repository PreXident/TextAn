package cz.cuni.mff.ufal.textan.server.setup.options;

import cz.cuni.mff.ufal.textan.server.setup.Setuper;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Simple holder of the options.
 */
public abstract class Command {

    /**
     * Accepts visitor Setuper.
     * @param setuper visitor
     * @throws SQLException if any SQL error occurs
     * @throws IOException if any IO error occurs
     */
    public abstract void accept(Setuper setuper) throws SQLException, IOException;
}
