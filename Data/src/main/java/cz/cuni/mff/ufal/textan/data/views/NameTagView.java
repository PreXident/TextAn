/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.views;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Vaclav Pernicka
 */
public class NameTagView implements INameTagView {

    @Autowired
    SessionFactory sessionFactory;
    
    @Override
    @SuppressWarnings("unchecked")
    public List<NameTagRecord> findAll() {
        Session s = sessionFactory.getCurrentSession();
        return s.createQuery(
                "select new NameTagRecord(doc.id, occ.position, alias.alias, objType.id)"
                        + "from DocumentTable doc"
                        + "     left join doc.aliasOccurrences occ"
                        + "     left join occ.alias alias"
                        + "     left join alias.object obj"
                        + "     left join obj.objectType objType"
                
        ).list();
    }
    
}
