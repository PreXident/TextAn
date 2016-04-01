package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTypeTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class ObjectTypeTableDAO extends AbstractHibernateDAO<ObjectTypeTable, Long> implements IObjectTypeTableDAO {

    /**
     * constructor
     */
    public ObjectTypeTableDAO() {
        super(ObjectTypeTable.class);
    }

}
