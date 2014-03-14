package cz.cuni.mff.ufal.textan.gui.graph;

import cz.cuni.mff.ufal.textan.core.graph.Grapher;
import cz.cuni.mff.ufal.textan.gui.WindowController;

/**
 *
 * @author hujeceka
 */
abstract class GraphController extends WindowController {

    /** Graph information provider. */
    protected Grapher grapher;

    /**
     * Sets graph information provider.
     * @param grapher new graph information provider
     */
    public void setGrapher(final Grapher grapher) {
        this.grapher = grapher;
    }
}
