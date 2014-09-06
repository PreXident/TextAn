package cz.cuni.mff.ufal.textan.tui;

import com.beust.jcommander.Parameter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple holder of the options.
 */
public class Options {

    /** Flag indicating whether help needs printing. */
    @Parameter(
            description = "prints help and exits",
            help = true,
            names = { "-h", "/H", "--help", "/?" })
    boolean help = false;

    /** Flag indicating whether reports should be force saved. */
    @Parameter(
            description = "reports should be force saved",
            names = { "-f", "/F", "--force" },
            arity = 1)
    boolean force = true;

    /** Flag indicating whether new objects should be created if none is recognized. */
    @Parameter(
            description = "create new objects if none is assigned",
            names = { "-n", "/N", "--new" },
            arity = 1)
    boolean newObjects = true;

    /** Flag indicating whether heuristic not to insert duplicite new objects should be used. */
    @Parameter(
            description = "use guessing heuristic not to insert duplicities",
            names = { "-g", "/G", "--guessing-heuristic" },
            arity = 1)
    boolean newObjectsHeuristic = true;

    /** Directory containing the reports to be processed. */
    @Parameter(
            description = "directory with reports to process, ignored when list of reports is provided",
            names = { "-d", "/D", "--directory" })
    String directory = null;

    /** Number of threads to use for processing. */
    @Parameter(
            description = "number of threads to use for processing; if <= 0 then auto",
            names = { "-t", "/T", "--threads" })
    int threads = 1;

    /** Path to settings file. */
    @Parameter(
            description = "settings file",
            names = { "-s", "/S", "--settings" })
    String settings = "TextAn.properties";

    /** List of reports to process. */
    @Parameter(
            description = "[list of reports to load]",
            converter = StringToFileConverter.class)
    List<File> files = new ArrayList<>();
}
