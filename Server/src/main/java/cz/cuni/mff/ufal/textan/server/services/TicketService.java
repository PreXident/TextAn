package cz.cuni.mff.ufal.textan.server.services;

import cz.cuni.mff.ufal.textan.data.repositories.dao.IGlobalVersionTableDAO;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import cz.cuni.mff.ufal.textan.server.models.EditingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Petr Fanta
 */
@Service
@Transactional
public class TicketService {

    private final IGlobalVersionTableDAO globalVersionTableDAO;

    @Autowired
    public TicketService(IGlobalVersionTableDAO globalVersionTableDAO) {
        this.globalVersionTableDAO = globalVersionTableDAO;
    }

    public EditingTicket createTicket() {
        return new EditingTicket(globalVersionTableDAO.getCurrentVersion());
    }
}
