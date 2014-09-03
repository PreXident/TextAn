/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 * @param <T> Table
 */
public interface IGlobalVersionedTableDAO<T> {
    /**
     * finds all records newer or equal from specified version
     * 
     * @param version global version which auto increments with object creation
     * @return 
     */
    List<T> findAllSinceGlobalVersion(long version); 
}
