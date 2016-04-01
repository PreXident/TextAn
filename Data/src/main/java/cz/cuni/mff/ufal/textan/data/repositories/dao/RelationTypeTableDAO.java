package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class RelationTypeTableDAO extends AbstractHibernateDAO<RelationTypeTable, Long> implements IRelationTypeTableDAO {

    /**
     * constructor
     */
    public RelationTypeTableDAO() {
        super(RelationTypeTable.class);
    }

}
