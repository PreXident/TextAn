package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common ancestor for commands loading types.
 */
public abstract class LoadTypes extends Command {

    /** List of reports to process. */
    @Parameter(
            description = "[list of files with types to load]",
            converter = StringToFileConverter.class)
    public List<File> files = new ArrayList<>();

    /** List of types to load. */
    public List<String> types = new ArrayList<>();

    /**
     * Prepares list of types loaded from files.
     * @throws IOException if any IO error occurs
     */
    protected void prepareTypes() throws IOException {
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        types.add(line);
                    }
                }
            } catch (Exception e) {
                System.out.printf("Error while loading file \"%s\"", file.getAbsolutePath());
                throw e;
            }
        }
    }
}
