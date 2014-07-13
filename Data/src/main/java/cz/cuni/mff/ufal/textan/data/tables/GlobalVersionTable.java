package cz.cuni.mff.ufal.textan.data.tables;

import javax.persistence.*;

/**
 * Global version of objects (for now) to decide if there was or created a new object
 *
 * @author Vaclav Pernicka
 * @author Petr Fanta
 */
@Entity
@Table(name = "GlobalVersion")
public class GlobalVersionTable extends AbstractTable {
    private long id;
    private long version;

    public GlobalVersionTable() {
    }

    @Id
    @GeneratedValue
    @Column(name = "id_global_version", nullable = false, unique = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "version", nullable = false)
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 41 * hash + (int) (this.version ^ (this.version >>> 32));
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
        final GlobalVersionTable other = (GlobalVersionTable) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        return true;
    }
}
