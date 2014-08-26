package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Common ancestor for commands renaming types.
 */
public abstract class RenameTypes extends Command {

    /** Flag indicating whether mapping is id based. */
    @Parameter(
            description = "input files contain mapping with ids, not names",
            help = true,
            names = { "-i", "/I", "--id" })
    public boolean id = false;

    /** List of reports to process. */
    @Parameter(
            description = "[list of files with types mapping]",
            converter = StringToFileConverter.class)
    public List<File> files = new ArrayList<>();

    /** Types mapping. */
    public Map<String, String> mapping = new TreeMap<>();

    /**
     * Prepares list of types loaded from files.
     */
    protected void prepareMapping() {
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String[] parts = line.split("=");
                    if (parts.length != 2) {
                        mapping.put(parts[0], parts[1]);
                    }
                }
            } catch (Exception e) {
                System.out.printf("Error while loading file \"%s\"", file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
}
