package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.DocumentTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

/**
 * Created by Petr Fanta on 31.3.2014.
 * @param <T> ObjectTable/RelationTable
 */
public interface IObjectRelationDAO<T> extends IOperations<T, Long> {
    List<T> findAllByAliasEqualTo(String alias);

    List<T> findAllByAliasSubstring(String aliasSubstring);

    List<T> findAllByDocumentOccurrence(Long documentId);
    List<T> findAllByDocumentOccurrence(DocumentTable document);
    
}
