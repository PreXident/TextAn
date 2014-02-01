package cz.cuni.mff.ufal.textan.commons.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Petr Fanta on 9.12.13.
 */

@XmlRootElement(name="document")
public class DocumentWithEntities extends Document {

    @XmlElementWrapper
    @XmlElement(name="entity")
    private List<Entity> entities = new ArrayList<Entity>();

    public DocumentWithEntities() {}

    public DocumentWithEntities(String text) {
        super(text);
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
