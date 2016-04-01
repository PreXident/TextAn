package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.InRelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class InRelationTableDAO extends AbstractHibernateDAO<InRelationTable, Long> implements IInRelationTableDAO {

    /**
     * constructor
     */
    public InRelationTableDAO() {
        super(InRelationTable.class);
    }

    @Override
    public List<String> getRolesForRelationType(RelationTypeTable relationType) {
        return getRolesForRelationType(relationType.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRolesForRelationType(long relationTypeId) {
        Query hq = currentSession().createQuery(
                "select i.role from InRelationTable as i "  //TODO: add sorting by count
                        + "inner join i.relation as rel "
                        + "inner join rel.relationType as relType "
                        + "where i.role != NULL and relType.id = :relationTypeId "
                        + "group by i.role"
        );
        hq.setParameter("relationTypeId", relationTypeId);

        return hq.list();
    }
}
