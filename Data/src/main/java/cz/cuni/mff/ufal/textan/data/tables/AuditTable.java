package cz.cuni.mff.ufal.textan.data.tables;

import cz.cuni.mff.ufal.textan.data.repositories.common.EnumUserType;
import java.util.Objects;

/**
 *
 * @author Václav Pernička
 */
public class AuditTable extends AbstractTable {
    public enum AuditType {
        Insert, Delete, Update, Read
    }
    
    private long id;
    private String username;
    private AuditType type;
    private String edit;

    public AuditTable() {
    }
   
    public AuditTable(String username, AuditType type, String edit) {
        this.username = username;
        this.type = type;
        this.edit = edit;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 83 * hash + Objects.hashCode(this.username);
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + Objects.hashCode(this.edit);
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
        final AuditTable other = (AuditTable) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.edit, other.edit)) {
            return false;
        }
        return true;
    }
    
}
