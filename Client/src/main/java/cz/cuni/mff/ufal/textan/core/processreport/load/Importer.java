/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.mff.ufal.textan.core.processreport.load;

/**
 * Interface for importing report texts from various file formats.
 */
public interface Importer {

    /**
     * Returns unique id of the importer.
     * @return unique id of the importer
     */
    String getId();

    /**
     * Extracts text from data.
     * @param data data file
     * @return text extracted from data
     */
    String extractText(byte[] data);
}
