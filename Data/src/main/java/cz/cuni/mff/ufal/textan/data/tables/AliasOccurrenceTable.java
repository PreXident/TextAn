package cz.cuni.mff.ufal.textan.data.tables;

/**
 *
 * @author Vaclav Pernicka
 */
public class AliasOccurrenceTable extends AbstractTable {
    /**
     * name of the document property
     */
    public static final String PROPERTY_NAME_DOCUMENT = "document";
    /**
     * name of the alias property
     */
    public static final String PROPERTY_NAME_ALIAS = "alias";

    private long id;
    private int position;
    
    private AliasTable alias;
    private DocumentTable document;

    /**
     *  no-param constructor
     */
    public AliasOccurrenceTable() {
    }

    /**
     * constructor
     * 
     * @param position position in the text
     * @param alias alias of the object
     * @param document 
     */
    public AliasOccurrenceTable(int position, AliasTable alias, DocumentTable document) {
        this.position = position;
        this.alias = alias;
        this.document = document;
    }

    /**
     *
     * @return id of the alias occurrence
     */
    public long getId() {
        return id;
    }

    /**
     * 
     * @param id id of the alias occurrence
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return position in the document
     */
    public int getPosition() {
        return position;
    }

    /**
     * 
     * @param position position in the document
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 
     * @return alias
     */
    public AliasTable getAlias() {
        return alias;
    }

    
    public void setAlias(AliasTable alias) {
        this.alias = alias;
    }

    public DocumentTable getDocument() {
        return document;
    }

    public void setDocument(DocumentTable document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return String.format("AliasOccurrence(%d, %d, %s, %s)", this.getId(), getPosition(), getAlias(), getDocument());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AliasOccurrenceTable)) return false;
        AliasOccurrenceTable aot = (AliasOccurrenceTable)o;
        if (aot.getId() != this.getId()) return false;
        return aot.getPosition() == this.getPosition();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 23 * hash + this.position;
        return hash;
    }
    
    
    
    
}
