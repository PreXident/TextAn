package cz.cuni.mff.ufal.textan.core.processreport.load;

import java.nio.charset.Charset;

/**
 * Extracts reports from UTF-8.
 */
public class TextCp1250 implements IImporter {

    /** Importer id. */
    static private final String ID = "TextCp1250";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String extractText(byte[] data) {
        try {
            return new String(data, Charset.forName("windows-1250"));
        } catch (Exception e) {
            return "";
        }
    }
}
