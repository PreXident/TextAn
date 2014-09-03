/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables.usertypes;

import cz.cuni.mff.ufal.textan.data.repositories.common.EnumUserType;
import cz.cuni.mff.ufal.textan.data.tables.AuditTable;

/**
 * Class for mapping enum AuditTable.AuditType to a varchar column
 * 
 * @see AuditTable.AuditType
 * 
 * @author Vaclav Pernicka
 */
public class AuditEnumUserType extends EnumUserType<AuditTable.AuditType> { 
    public AuditEnumUserType() { 
        super(AuditTable.AuditType.class); 
    } 
}
