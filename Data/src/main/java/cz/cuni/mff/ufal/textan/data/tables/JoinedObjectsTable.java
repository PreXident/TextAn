/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Table that is able to cover joining two objects into one
 * @author Vaclav Pernicka
 */
@Entity
@Table(name = "JoinedObjects")
public class JoinedObjectsTable extends AbstractTable {
    private long id;
    private Date from;
    private Date to;
    private ObjectTable newObject;
    private ObjectTable oldObject1;    
    private ObjectTable oldObject2;

    public JoinedObjectsTable() {
    }
    
    /**
 * Sets the "from Date" to current date and "to Date" to null which means infinity
 * 
 * @param newObject
 * @param oldObject1
 * @param oldObject2 
 */
    public JoinedObjectsTable(ObjectTable newObject, ObjectTable oldObject1, ObjectTable oldObject2) {
        this(Calendar.getInstance().getTime(), null, newObject, oldObject1, oldObject2);
    }

    /**
     * 
     * @param from The date, objects are joined from
     * @param to The date, objects are joined to. Null for infinity
     * @param newObject New joined object.
     * @param oldObject1
     * @param oldObject2 
     */
    public JoinedObjectsTable(Date from, Date to, ObjectTable newObject, ObjectTable oldObject1, ObjectTable oldObject2) {
        this.from = from;
        this.to = to;
        this.newObject = newObject;
        this.oldObject1 = oldObject1;
        this.oldObject2 = oldObject2;
    }

    @Id
    @Column(name = "id_new_object", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "from_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    @Column(name = "to_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @OneToOne //todo
    public ObjectTable getNewObject() {
        return newObject;
    }

    public void setNewObject(ObjectTable newObject) {
        this.newObject = newObject;
        this.setId(newObject.getId());
    }

    @OneToOne //todo
    public ObjectTable getOldObject1() {
        return oldObject1;
    }

    public void setOldObject1(ObjectTable oldObject1) {
        this.oldObject1 = oldObject1;
    }

    @OneToOne //todo
    public ObjectTable getOldObject2() {
        return oldObject2;
    }

    public void setOldObject2(ObjectTable oldObject2) {
        this.oldObject2 = oldObject2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 83 * hash + Objects.hashCode(this.newObject);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JoinedObjectsTable other = (JoinedObjectsTable) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.newObject, other.newObject);
    }

    
    
    @Override
    public String toString() {
        return "JoinedObjectsTable{" + "id=" + id + ", from=" + from + ", to=" + to + ", newObject=" + newObject + ", oldObject1=" + oldObject1 + ", oldObject2=" + oldObject2 + '}';
    }
    
}
