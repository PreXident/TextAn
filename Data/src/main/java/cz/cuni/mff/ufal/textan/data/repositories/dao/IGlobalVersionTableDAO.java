package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;

/**
 * @author Vaclav Pernicka
 */
public interface IGlobalVersionTableDAO extends IOperations<GlobalVersionTable, Long> {

    /**
     * Gets the current global version
     *
     * @return current global version
     */
    long getCurrentVersion();
}
