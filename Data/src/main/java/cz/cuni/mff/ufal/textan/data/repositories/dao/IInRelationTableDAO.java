/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories.dao;

import cz.cuni.mff.ufal.textan.data.repositories.common.IOperations;
import cz.cuni.mff.ufal.textan.data.tables.InRelationTable;
import cz.cuni.mff.ufal.textan.data.tables.RelationTypeTable;

import javax.management.relation.RelationType;
import java.util.List;

/**
 *
 * @author Vaclav Pernicka
 */
public interface IInRelationTableDAO extends IOperations<InRelationTable, Long> {

    List<String> getRolesForRelationType(RelationTypeTable relationType);
    List<String> getRolesForRelationType(long relationTypeId);
}
