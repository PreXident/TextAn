/*
 *  Created by Vaclav Pernicka
 */

package cz.cuni.mff.ufal.textan.data.views;

import javax.persistence.Entity;
import java.util.Objects;

/**
 *
 * @author Vaclav Pernicka
 */
@Entity
public class NameTagRecord {
    Long DocumentID;
    Integer aliasOccurrencePosition;
    String alias;
    Long objectTypeID;

    public NameTagRecord(Long DocumentID, Integer aliasOccurrencePosition, String alias, Long objectTypeID) {
        this.DocumentID = DocumentID;
        this.aliasOccurrencePosition = aliasOccurrencePosition;
        this.alias = alias;
        this.objectTypeID = objectTypeID;
    }

    @Override
    public String toString() {
        return "NameTagView{" + "DocumentID=" + DocumentID + ", aliasOccurrencePosition=" + aliasOccurrencePosition + ", alias=" + alias + ", objectTypeID=" + objectTypeID + '}';
    }

    public long getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(long DocumentID) {
        this.DocumentID = DocumentID;
    }

    public int getAliasOccurrencePosition() {
        return aliasOccurrencePosition;
    }

    public void setAliasOccurrencePosition(int aliasOccurrencePosition) {
        this.aliasOccurrencePosition = aliasOccurrencePosition;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getObjectTypeID() {
        return objectTypeID;
    }

    public void setObjectTypeID(long objectTypeID) {
        this.objectTypeID = objectTypeID;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.DocumentID ^ (this.DocumentID >>> 32));
        hash = 41 * hash + this.aliasOccurrencePosition;
        hash = 41 * hash + Objects.hashCode(this.alias);
        hash = 41 * hash + (int) (this.objectTypeID ^ (this.objectTypeID >>> 32));
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
        final NameTagRecord other = (NameTagRecord) obj;
        if (this.DocumentID != other.DocumentID) {
            return false;
        }
        if (this.aliasOccurrencePosition != other.aliasOccurrencePosition) {
            return false;
        }
        if (!Objects.equals(this.alias, other.alias)) {
            return false;
        }
        if (this.objectTypeID != other.objectTypeID) {
            return false;
        }
        return true;
    }
    
    
}
