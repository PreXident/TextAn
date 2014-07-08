// *************** INTERFACE*******************
package cz.cuni.mff.ufal.textan.textpro.data;
/*
 * All characteristics of an Entity
 */

public class Entity{

    /*
     * Change type from String to long
     * Default constructor
    */
    public Entity(String text, int offset, int position, long typeID) {
        this.text = text;
        this.offset = offset;
        this.position = position;
        this.type = typeID;
    }
    
    /*
     * The words in ducument
     */
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
 
   
    /*
     * Length of the entity
     */
    int offset;
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }    
    
    /*
     * position of the entity
     */
    int position;

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    
    /*
     * type of the entity
     * Associate with the PROPERTY_NAME_ID of object table
     */
    long type;
    public long getType() {
        return type;
    }
    public void setType(long type) {
        this.type = type;
    }    
    public boolean equals (Entity e2) {
        if(this.offset != e2.offset) return false;
        return true;
    }
        
}
