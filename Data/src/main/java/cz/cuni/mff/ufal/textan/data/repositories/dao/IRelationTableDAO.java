package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;

import java.util.List;


/**
 * DAO interface to get Relations
 * 
 */
public interface IRelationTableDAO extends IObjectRelationDAO<RelationTable> {

    /**
     * finds all relations of specified type
     * 
     * @param relationTypeId id of the relation type
     * @return 
     */
    List<RelationTable> findAllByRelationType(Long relationTypeId);
    /**
     * finds all relations of specified type
     * 
     * @param type
     * @return 
     */
    List<RelationTable> findAllByRelationType(RelationTypeTable type);

}
