package cz.cuni.mff.ufal.textan.data.tables;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.hibernate.annotations.GenericGenerator;




/**
 * Table that is able to cover joining two objects into one
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "JoinedObjects")
public class JoinedObjectsTable extends AbstractTable {
    public static final String PROPERTY_NAME_GLOBAL_VERSION = "globalVersion";
    
    private long id;
    private Date from;
    private Date to;
    private ObjectTable newObject;
    private ObjectTable oldObject1;
    private ObjectTable oldObject2;
    private long globalVersion;

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
     * @param from       The date, objects are joined from
     * @param to         The date, objects are joined to. Null for infinity
     * @param newObject  New joined object.
     * @param oldObject1
     * @param oldObject2
     */
    public JoinedObjectsTable(Date from, Date to, ObjectTable newObject, ObjectTable oldObject1, ObjectTable oldObject2) {
        this.from = from;
        this.to = to;
        
        this.setNewObject(newObject);
        this.newObject.setNewObject(this);
        
        this.oldObject1 = oldObject1;
        this.oldObject1.setOldObjects1(this);
        
        this.setOldObject2(oldObject2);
        this.oldObject2.setOldObjects2(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_joined_object", nullable = false, unique = true)
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

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_new_object", nullable = false)
    public ObjectTable getNewObject() {
        return newObject;
    }

    public void setNewObject(ObjectTable newObject) {
        this.newObject = newObject;
        //this.setId(newObject.getId());
    }

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_old_object1", nullable = false)
    public ObjectTable getOldObject1() {
        return oldObject1;
    }

    public void setOldObject1(ObjectTable oldObject1) {
        this.oldObject1 = oldObject1;
    }

    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "id_old_object2", nullable = false)
    public ObjectTable getOldObject2() {
        return oldObject2;
    }

    public void setOldObject2(ObjectTable oldObject2) {
        this.oldObject2 = oldObject2;
    }
  
    @Column(name = "globalversion", nullable = false)
    public long getGlobalVersion() {
        return globalVersion;
    }

    public void setGlobalVersion(long globalVersion) {
        this.globalVersion = globalVersion;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 47 * hash + (int) (this.globalVersion ^ (this.globalVersion >>> 32));
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
        if (this.globalVersion != other.globalVersion) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "JoinedObjectsTable{" + "id=" + id + ", globalVersion=" + globalVersion + ", from=" + from + ", to=" + to + ", newObject=" + newObject + ", oldObject1=" + oldObject1 + ", oldObject2=" + oldObject2 + '}';
    }


}
