package cz.cuni.mff.ufal.textan.commons;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Petr Fanta
 * Date: 19.11.13
 * Time: 22:09
 */

/**
 * Base class for Document data model.
 */
@XmlRootElement
public class Document {

    @XmlElement
    private String text;

    public Document() {}

    public Document(String text) {
        setText(text);
    }

    /**
     * Gets text of document.
     * @return text The text of document.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text of document.
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }


}
