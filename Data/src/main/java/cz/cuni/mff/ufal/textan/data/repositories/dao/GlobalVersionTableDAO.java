package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class GlobalVersionTableDAO extends AbstractHibernateDAO<GlobalVersionTable, Long> implements IGlobalVersionTableDAO {

    /**
     * constructor
     */
    public GlobalVersionTableDAO() {
        super(GlobalVersionTable.class);
    }

    @Override
    public long getCurrentVersion() {
        GlobalVersionTable gvt = (GlobalVersionTable) currentSession().createCriteria(GlobalVersionTable.class)
                .setMaxResults(1)
                .list().get(0);
        return gvt.getVersion();
    }

}
