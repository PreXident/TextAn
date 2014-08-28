package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Command for creating training data.
 */
@Parameters(commandDescription = "Prepare training data")
public class PrepareTrainingData extends Command {

    /** Path to file containing settings. */
    @Parameter(
            description = "nametag learning properties",
            names = { "-l", "/L", "--learning" })
    public String learning = "learning.properties";

    /** File containing the mapping */
    @Parameter(
            description = "file with mapping if types in training data does not match type names in the database",
            names = { "-m", "/M", "--mapping" })
    public String mappingFile = null;

    /**
     * Mapping for renaming.
     * When getting the value for key, if key is mapped to null, key.toString()
     * is returned.
     */
    public Map<String, String> mapping = new HashMap<String, String>() {
        @Override
        public String get(final Object key) {
            final String result = super.get(key);
            if (result != null) {
                return result;
            } else {
                return key != null ? key.toString() : null;
            }
        }
    };

    @Override
    public void accept(final Setuper setuper) {
        prepareMapping();
        setuper.prepareTrainingData(this);
    }

    /**
     * Prepares list of types loaded from files.
     */
    protected void prepareMapping() {
        try (BufferedReader reader = new BufferedReader(new FileReader(mappingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split("=");
                if (parts.length != 2) {
                    mapping.put(parts[0], parts[1]);
                }
            }
        } catch (Exception e) {
            System.out.printf("Error while loading file \"%s\"", mappingFile);
            e.printStackTrace();
        }
    }
}
