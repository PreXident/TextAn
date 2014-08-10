package cz.cuni.mff.ufal.textan;

/**
 * A few useful static methods.
 */
public class Utils {

    /**
     * Extracts extension from file name.
     * @param name file name
     * @return extension from file name
     */
    static public String extractExtension(final String name) {
        final int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return name.substring(dot + 1);
    }

    /** Utility class, no instantiation. */
    private Utils() { }
}
