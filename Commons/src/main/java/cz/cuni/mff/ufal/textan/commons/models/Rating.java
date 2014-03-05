package cz.cuni.mff.ufal.textan.commons.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * Holds information about object candidates.
 */
public class Rating {

    @XmlElement
    public Object[] candidate = new Object[0];

    @XmlElement
    public double[] rating = new double[0];
}
