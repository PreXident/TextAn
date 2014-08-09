package cz.cuni.mff.ufal.textan.core.processreport.load;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for managering {@link Importer}s.
 */
public class ImportManager {

    /** Mapping of extensions to default importers. */
    private static final Map<String, Importer> extensions;

    /** Mapping of ids to importers. */
    private static final Map<String, Importer> ids;

    static {
        final Importer cp1250 = new TextCp1250();
        final Importer utf8 = new TextUtf8();
        extensions = new HashMap<>();
        extensions.put("txt", utf8);
        ids = new HashMap<>();
        ids.put(utf8.getId(), utf8);
        ids.put(cp1250.getId(), cp1250);
    }

    /**
     * Returns default FileType for given extension.
     * @param extension given extension
     * @return default FileType for given extension or null if none available
     */
    public static Importer getDefaultForExtension(final String extension) {
        return extensions.get(extension);
    }

    /**
     * Registers default importer for given extension.
     * @param extension extension to register
     * @param importer importer to register
     */
    public static void registerDefaultExtension(final String extension,
            final Importer importer) {
        extensions.put(extension, importer);
    }

    /**
     * Registers importer.
     * @param importer importer to register
     */
    public static void registerImporter(final Importer importer) {
        ids.put(importer.getId(), importer);
    }

    /**
     * Returns importer with given id.
     * @param id importer id
     * @return importer with given id
     */
    public static Importer getImporter(final String id) {
        return ids.get(id);
    }

    /**
     * Returns unmodifiable collection of importers.
     * @return unmodifiable collection of importers
     */
    public static Collection<Importer> getImporters() {
        return Collections.unmodifiableCollection(ids.values());
    }
}
