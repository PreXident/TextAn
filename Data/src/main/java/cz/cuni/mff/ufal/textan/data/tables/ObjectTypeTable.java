/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Václav Pernička
 */

@Entity
@Table( name = "ObjectType" )
public class ObjectTypeTable {
 
    private long id;
    private String name;


    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    long getID() {
        return this.id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    
}
