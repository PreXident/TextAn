package cz.cuni.mff.ufal.textan.core.processreport.load;

/**
 * Interface for importing report texts from various file formats.
 */
public interface IImporter {

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
