/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.views;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaclav Pernicka
 */
@Transactional
public class NameTagView implements INameTagView {

    SessionFactory sessionFactory;

    public NameTagView(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
    
    @Override
    @SuppressWarnings("unchecked")
    public List<NameTagRecord> findAll() {
        Session s = sessionFactory.getCurrentSession();
        return s.createQuery(
                "select new NameTagRecord(doc.id, occ.position, al.alias, objType.id)"
                        + "from DocumentTable doc"
                        + "     left join doc.aliasOccurrences occ"
                        + "     left join occ.alias al"
                        + "     left join al.object obj"
                        + "     left join obj.objectType objType"
                
        ).list();
    }
    
}
