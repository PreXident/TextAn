/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.AliasOccurrenceTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class AliasOccurrenceTableDAO extends AbstractHibernateDAO<AliasOccurrenceTable, Long> implements IAliasOccurrenceTableDAO {

    /**
     *
     */
    public AliasOccurrenceTableDAO() {
        super(AliasOccurrenceTable.class);
    }
    
}
