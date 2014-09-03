package cz.cuni.mff.ufal.textan.server.setup.options;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import cz.cuni.mff.ufal.textan.server.setup.Setuper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Command for creating training data.
 */
@Parameters(commandDescription = "Prepare training data.")
public class PrepareTrainingData extends Command {

    /** Path to file containing settings. */
    @Parameter(
            description = "nametag learning properties",
            names = { "-l", "/L", "--learning" },
            required = true )
    public String learning = "learning.properties";

    /** File containing the mapping */
    @Parameter(
            description = "File with mapping if types in training data does not match type names in the database.",
            names = { "-m", "/M", "--mapping" })
    public String mappingFile = null;

    /** Used IDs instead of names in type mapping to database */
    @Parameter(
            description = "Use IDs instead of type name in type mapping to database.",
            names = { "-i", "/I", "--useid" })
    public boolean useIdMapping = false;

    /** Set property to translated training data */
    @Parameter(
            description = "Set property to translated training data.",
            names = { "-s", "/S", "--set" })
    public boolean setProperty = false;

    /** Used IDs instead of names in type mapping to database */
    @Parameter(
            description = "Output file for translated training data.",
            names = { "-o", "/O", "--out" },
            required = true)
    public String outputFile = null;

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
    public void accept(final Setuper setuper) throws IOException, SQLException {
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
