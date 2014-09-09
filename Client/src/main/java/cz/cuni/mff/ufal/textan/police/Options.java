package cz.cuni.mff.ufal.textan.police;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Main options for the Setuper.
 */
public class Options {

    /** Map of supported commands. */
    protected final Map<String, Command> commands = new TreeMap<>();

    /** JCommander parsing options. */
    protected final JCommander jCommander;

    /** Flag indicating whether help needs printing. */
    @Parameter(
            description = "Prints help and exits.",
            help = true,
            names = { "-h", "/H", "--help", "/?" })
    public boolean help = false;

    /**
     * Only constructor.
     * @param args command line arguments
     */
    public Options(final String[] args) {
        commands.put(ExtractTypes.NAME, new ExtractTypes());
        commands.put(PrepareTraining.NAME, new PrepareTraining());
        commands.put(FixXml.NAME, new FixXml());
        //
        jCommander = new JCommander(this);
        for (Entry<String, Command> entry : commands.entrySet()) {
            jCommander.addCommand(entry.getKey(), entry.getValue());
        }
        jCommander.setProgramName("java -cp TextAn-client.jar " + Policer.class.getName()); //set name in usage
        jCommander.parse(args);
    }

    /**
     * Returns command demanded by user.
     * @return command registered for the name
     */
    public Command getCommand() {
        final String commandName = jCommander.getParsedCommand();
        return commandName == null ? null : commands.get(commandName);
    }

    public void printUsage() {
        jCommander.usage();
    }
}
