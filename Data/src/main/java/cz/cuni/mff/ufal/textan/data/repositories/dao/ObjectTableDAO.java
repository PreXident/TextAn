/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;


import cz.cuni.mff.ufal.textan.data.repositories.common.AbstractHibernateDAO;
import cz.cuni.mff.ufal.textan.data.tables.ObjectTable;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Václav Pernička
 */
@Repository
@Deprecated // TODO: Not finished yet
public class ObjectTableDAO extends AbstractHibernateDAO<ObjectTable, Long> {
    // TODO: Takhle to budu delat pro vsechny sloupecky v kazdy tabulce?
    private static final String PROPERTY_NAME_OBJECT_TYPE_ID = "objectType";
    
    public ObjectTableDAO() {
        super(ObjectTable.class);
    }
    
    public List<ObjectTable> findAllByObjectType(Long objectTypeId) {
        return super.findAllByProperty(PROPERTY_NAME_OBJECT_TYPE_ID, objectTypeId);        
    }
}
