package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;

import java.util.List;

public interface IObjectTableDAO extends IObjectRelationDAO<ObjectTable> {
    List<ObjectTable> findAllByObjectType(Long objectTypeId);

    List<ObjectTable> findAllByObjectType(ObjectTypeTable type);
}
