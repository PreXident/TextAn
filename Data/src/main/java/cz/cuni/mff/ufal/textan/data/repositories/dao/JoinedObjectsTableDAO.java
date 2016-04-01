package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.exceptions.JoiningANonRootObjectException;
import cz.cuni.mff.ufal.textan.data.exceptions.JoiningEqualObjectsException;
import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.JoinedObjectsTable;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class JoinedObjectsTableDAO extends AbstractHibernateDAO<JoinedObjectsTable, Long> implements IJoinedObjectsTableDAO {

    @Autowired
    IObjectTableDAO objectDAO;

    /**
     * constructor
     */
    public JoinedObjectsTableDAO() {
        super(JoinedObjectsTable.class);
    }

    @Override
    public ObjectTable join(ObjectTable obj1, ObjectTable obj2) throws JoiningANonRootObjectException, JoiningEqualObjectsException {

        if (!obj1.isRoot()) throw new JoiningANonRootObjectException(obj1);
        if (!obj2.isRoot()) throw new JoiningANonRootObjectException(obj2);
        if (obj1.equals(obj2)) throw new JoiningEqualObjectsException();

        ObjectTable newObj = new ObjectTable("join(" + obj1.getData() + ", " + obj2.getData() + ")", obj1.getObjectType());
        JoinedObjectsTable joinedObj = new JoinedObjectsTable(newObj, obj1, obj2);

        objectDAO.add(newObj);
        add(joinedObj);

        FullTextSession fullTextSession = Search.getFullTextSession(currentSession());

        setRootInSubTree(obj1, newObj, fullTextSession);
        setRootInSubTree(obj2, newObj, fullTextSession);

        return newObj;
    }

    private void setRootInSubTree(ObjectTable obj, ObjectTable root, FullTextSession fullTextSession) {
        obj.setRootObject(root);
        obj.getRootOfObjects().clear(); // clear don't make persistent object dirty, so ve need make index manually, or set new empty list
        root.getRootOfObjects().add(obj);

        fullTextSession.index(obj);

        if (obj.getNewObject() == null) return;

        setRootInSubTree(obj.getNewObject().getOldObject1(), root, fullTextSession);
        setRootInSubTree(obj.getNewObject().getOldObject2(), root, fullTextSession);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<JoinedObjectsTable> findAllSinceGlobalVersion(long version) {
        return findAllCriteria()
                .add(Restrictions.ge(JoinedObjectsTable.PROPERTY_NAME_GLOBAL_VERSION, version))
                .list();
    }
}
