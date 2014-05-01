package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.commons.utils.Pair;
import cz.cuni.mff.ufal.textan.data.repositories.dao.IObjectTableDAO;
import cz.cuni.mff.ufal.textan.server.models.*;
import cz.cuni.mff.ufal.textan.server.models.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A service which provides a saving of a processed document.
 *
 * @author Petr Fanta
 */
@Service
@Transactional
public class SaveService {

    private final IObjectTableDAO objectTableDAO;

    @Autowired
    public SaveService(IObjectTableDAO objectTableDAO) {
        this.objectTableDAO = objectTableDAO;
    }


    /*
    * TODO:
    *  - send only new object / relations?
    *  - relation occurrence?
    *  - u klienta nejsou zname ID!
    *
    * */


    public boolean save(
            String text, List<Pair<Entity, Object>> entityObjectAssignments, List<Relation> relations, boolean force, EditingTicket serverTicket) {

        if (!force && checkChanges()) {
            return false;
        }



        return true;
    }

    public boolean save(long documentId, List<Pair<Entity, Object>> entityObjectAssignments, List<Relation> relations, boolean force, EditingTicket serverTicket) throws IdNotFoundException{

        if (!force && checkChanges()) {
            return false;
        }

        return true;
    }

    private boolean checkChanges() {
        return false;
    }
}
