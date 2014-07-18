/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.GlobalVersionTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vaclav Pernicka
 */
@Repository
@Transactional
public class GlobalVersionTableDAO extends AbstractHibernateDAO<GlobalVersionTable, Long> implements IGlobalVersionTableDAO {

    /**
     *  constructor
     */
    public GlobalVersionTableDAO() {
        super(GlobalVersionTable.class);
    }

    @Override
    public long getCurrentVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
