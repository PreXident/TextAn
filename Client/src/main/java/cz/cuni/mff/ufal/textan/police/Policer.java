package cz.cuni.mff.ufal.textan.police;

import com.beust.jcommander.ParameterException;

/**
 * Utility class intended to handle various tasks related to police domain.
 */
public class Policer {

    /** Error code when arguments are incorrect. */
    static public final int ARGUMENT_ERROR = 1;

    /** Error code when no command is provided. */
    static public final int NO_COMMAND = 2;

    /** Error code when unexpected error occurs. */
    static public final int UNEXPECTED_ERROR = 3;

    /** Error code when xml parsing failed. */
    static public final int XML_ERROR = 4;

    /** Error code when sax factory creation failed. */
    static public final int SAX_ERROR = 5;

    /** Error code when io error occurs. */
    static public final int IO_ERROR = 5;

    /**
     * Main function.
     * @param args command line arguments
     */
    static public void main(final String[] args) {
        try {
            //parse command line options
            final Options options = new Options(args);
            final Command command = options.getCommand();
            if (options.help || command == null || command.checkParameters() != 0) {
                options.printUsage();
                System.exit(command == null ? 0 : NO_COMMAND);
            }
            final int exit_code = command.execute(options);
            System.exit(exit_code);
        } catch (Throwable t) {
            //handle t as uncaught exception but set exit code
            final Thread thread = Thread.currentThread();
            final Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();
            if (handler != null) {
                handler.uncaughtException(thread, t);
            } else {
                thread.getThreadGroup().uncaughtException(thread, t);
            }
            System.exit(t instanceof ParameterException ? ARGUMENT_ERROR : UNEXPECTED_ERROR);
        }
    }

    /**
     * Utility classes need no constructor.
     */
    private Policer() {
        //nothing
    }
}
