package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Václav Pernička
 */

@Entity
@Table( name = "Object" )
public class ObjectTable extends AbstractTable {
    
    private long id;
    private String date;
    
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")   
    long getID() {
        return this.id;
    }
    

    @Column(name = "data")
    public String getData() {
        return date;
    }
    
}
