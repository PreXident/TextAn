package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.exceptions.JoiningANonRootObjectException;
import cz.cuni.mff.ufal.textan.data.exceptions.JoiningEqualObjectsException;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IJoinedObjectsTableDAO;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;

/**
 * @author Petr Fanta
 */
@Service
@Transactional
public class MergeService {

    private final IObjectTableDAO objectTableDAO;
    private final IJoinedObjectsTableDAO joinedObjectsTableDAO;

    private final Lock writeLock;

    @Autowired
    public MergeService(IObjectTableDAO objectTableDAO, IJoinedObjectsTableDAO joinedObjectsTableDAO, @Qualifier("writeLock") Lock writeLock) {
        this.objectTableDAO = objectTableDAO;
        this.joinedObjectsTableDAO = joinedObjectsTableDAO;
        this.writeLock = writeLock;
    }

    /**
     * Merges two objects into one.
     *
     * @param object1Id the identifier of the first object
     * @param object2Id the identifier of the second object
     * @return the identifier of the new object
     * @throws IdNotFoundException when no objects of given ids exist
     * @throws InvalidMergeException if objects have different types
     * @throws NonRootObjectException if one of the objects is not root
     */
    public long mergeObjects(long object1Id, long object2Id) throws IdNotFoundException, InvalidMergeException, NonRootObjectException {

        ObjectTable objectTable1 = objectTableDAO.find(object1Id);
        if (objectTable1 == null) {
            throw new IdNotFoundException("object1Id", object1Id);
        }

        ObjectTable objectTable2 = objectTableDAO.find(object2Id);
        if (objectTable2 == null) {
            throw new IdNotFoundException("object2Id", object2Id);
        }

        if (!objectTable1.getObjectType().equals(objectTable2.getObjectType())) {
            throw new InvalidMergeException("Objects have different type.");
        }

        writeLock.lock();
        try {
            return joinedObjectsTableDAO.join(objectTable1, objectTable2).getId();
        } catch (JoiningANonRootObjectException e) {
            ObjectTable invalidObject = (ObjectTable)e.getTag();
            throw new NonRootObjectException(invalidObject.getId(), invalidObject.getRootObject().getId());
        } catch (JoiningEqualObjectsException e) {
            long invalidObjectId = ((ObjectTable)e.getTag()).getId();
            throw new InvalidMergeException("Merging objects are equal.", invalidObjectId);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Splits merged object.
     *
     * @param objectId the identifier of merged object
     * @return true if object was split, false otherwise
     * @throws IdNotFoundException if no object with the given id exists
     * @throws NonRootObjectException if given object is not root
     */
    public boolean splitObject(long objectId) throws IdNotFoundException, NonRootObjectException {
        //TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
