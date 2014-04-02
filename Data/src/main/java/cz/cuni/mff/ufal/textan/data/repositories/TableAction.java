/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.repositories;

import cz.cuni.mff.ufal.textan.data.tables.AbstractTable;


/**
 *
 * @author Vaclav Pernicka
 * @param <Table> Class representing a table in database. It's also a child of AbstractTable.
 */
public interface TableAction<Table extends AbstractTable> {
    void action(Table table);
}
