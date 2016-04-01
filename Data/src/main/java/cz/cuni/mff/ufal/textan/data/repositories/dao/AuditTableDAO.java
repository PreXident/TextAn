package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.AuditTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class AuditTableDAO extends AbstractHibernateDAO<AuditTable, Long> implements IAuditTableDAO {

    /**
     * constructor
     */
    public AuditTableDAO() {
        super(AuditTable.class);
    }

}
