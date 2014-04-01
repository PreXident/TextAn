package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.RelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;

import java.util.List;


public interface IRelationTableDAO extends IObjectRelationDAO<RelationTable> {

    List<RelationTable> findAllByRelationType(Long relationTypeId);
    List<RelationTable> findAllByRelationType(RelationTypeTable type);

}
