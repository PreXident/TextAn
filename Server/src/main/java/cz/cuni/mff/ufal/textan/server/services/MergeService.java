package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Petr Fanta
 */
@Service
@Transactional
public class MergeService {

    private final IObjectTableDAO objectTableDAO;
    private final IJoinedObjectsTableDAO joinedObjectsTableDAO;

    @Autowired
    public MergeService(IObjectTableDAO objectTableDAO, IJoinedObjectsTableDAO joinedObjectsTableDAO) {
        this.objectTableDAO = objectTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
    }

    /**
     * Merges two objects into one.
     *
     * @param object1Id the identifier of the first object
     * @param object2Id the identifier of the second object
     * @return the identifier of the new object
     */
    public long mergeObjects(long object1Id, long object2Id) throws IdNotFoundException {

        ObjectTable objectTable1 = objectTableDAO.find(object1Id);
        if (objectTable1 == null) {
            throw new IdNotFoundException("object1Id", object1Id);
        } else if (!objectTable1.isRoot()) {
            //TODO throw some exception
            return -1;
        }

        ObjectTable objectTable2 = objectTableDAO.find(object2Id);
        if (objectTable2 == null) {
            throw new IdNotFoundException("object2Id", object2Id);
        } else if (!objectTable2.isRoot()) {
            //TODO: throw some exception
            return -1;
        }

        return joinedObjectsTableDAO.join(objectTable1, objectTable2).getId();
    }

    /**
     * Splits merged object.
     *
     * @param objectId the identifier of merged object
     * @return true if object was split, false otherwise
     */
    public boolean splitObject(long objectId) throws IdNotFoundException {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
