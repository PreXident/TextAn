package cz.cuni.mff.ufal.textan.data.tables;

/**
 *
 * @author Václav Pernička
 */
public class GlobalVersionTable extends AbstractTable {
    private long version;

    public GlobalVersionTable() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.version ^ (this.version >>> 32));
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
        if (this.version != other.version) {
            return false;
        }
        return true;
    }
    
    
}
