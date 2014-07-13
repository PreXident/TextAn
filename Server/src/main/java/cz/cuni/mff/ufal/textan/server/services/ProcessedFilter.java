package cz.cuni.mff.ufal.textan.server.services;

/**
 * @author Petr Fanta
 */
public enum ProcessedFilter {
    ALL, PROCESSED, UNPROCESSED;

    public static ProcessedFilter parse(String str) {
        if (str == null) return null;

        String lowerCasedStr = str.toLowerCase();
        if (str.equals("all")) {
            return ALL;
        } else if (str.equals("processed")) {
            return PROCESSED;
        } else if (str.equals("unprocessed")) {
            return UNPROCESSED;
        } else {
            return null;
        }
    }
}
