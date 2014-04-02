/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.RelationOccurrenceTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Václav Pernička
 */
@Repository
@Transactional
public class RelationOccurrenceTableDAO extends AbstractHibernateDAO<RelationOccurrenceTable, Long> implements IRelationOccurrenceTableDAO {

    public RelationOccurrenceTableDAO() {
        super(RelationOccurrenceTable.class);
    }
    
}
