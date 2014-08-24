package cz.cuni.mff.ufal.textan.core.processreport.load;

import java.nio.charset.StandardCharsets;

/**
 * Extracts reports from UTF-8.
 */
public class TextUtf8 implements IImporter {

    /** Importer id. */
    static private final String ID = "TextUtf8";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String extractText(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
