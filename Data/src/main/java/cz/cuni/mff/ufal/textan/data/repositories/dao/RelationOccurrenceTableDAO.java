package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class RelationOccurrenceTableDAO extends AbstractHibernateDAO<RelationOccurrenceTable, Long> implements IRelationOccurrenceTableDAO {

    /**
     * constructor
     */
    public RelationOccurrenceTableDAO() {
        super(RelationOccurrenceTable.class);
    }

}
