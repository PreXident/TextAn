/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.views.EntityView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class EntityViewDAO implements IEntityViewDAO {

    SessionFactory sessionFactory;

    @Autowired
    public EntityViewDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public List<EntityView> findAll() {
        Session s = sessionFactory.getCurrentSession();
        return s.createQuery(
                "select new cz.cuni.mff.ufal.textan.data.views.EntityView(doc.id, occ.position, al.alias, objType.id)"
                        + "from DocumentTable doc"
                        + "     inner join doc.aliasOccurrences occ"
                        + "     inner join occ.alias al"
                        + "     inner join al.object obj"
                        + "     inner join obj.objectType objType"
                
        ).list();
    }
    
}
