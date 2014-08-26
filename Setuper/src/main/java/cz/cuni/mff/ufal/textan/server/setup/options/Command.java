package cz.cuni.mff.ufal.textan.server.setup.options;

import cz.cuni.mff.ufal.textan.server.setup.Setuper;

/**
 * Simple holder of the options.
 */
public abstract class Command {

    /**
     * Accepts visitor Setuper.
     * @param setuper visitor
     */
    public abstract void accept(Setuper setuper);
}
