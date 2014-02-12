/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

/**
 *
 * @author Václav Pernička
 */
public class AliasTable extends AbstractTable {
    private long id;
    private ObjectTable object;
    private String alias;

    public AliasTable() {
    }

    public AliasTable(ObjectTable object, String alias) {
        this.object = object;
        this.alias = alias;
    }

    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ObjectTable getObject() {
        return object;
    }

    public void setObject(ObjectTable object) {
        this.object = object;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    
}
