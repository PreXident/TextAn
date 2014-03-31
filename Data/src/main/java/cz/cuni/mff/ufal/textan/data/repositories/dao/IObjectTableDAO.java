package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

/**
 * Created by Petr Fanta on 31.3.2014.
 */
public interface IObjectTableDAO extends IOperations<ObjectTable, Long> {
    List<ObjectTable> findAllByObjectType(Long objectTypeId);

    List<ObjectTable> findAllByObjectType(ObjectTypeTable objectType);

    List<ObjectTable> findAllByAliasEqualTo(String alias);

    List<ObjectTable> findAllByAliasSubstring(String aliasSubstring);

    List<ObjectTable> findAllByDocumentOccurrence(Long documentId);
}
