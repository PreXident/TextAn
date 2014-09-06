package cz.cuni.mff.ufal.textan.police;

/**
 * Ancestor for all commands.
 */
public abstract class Command {

    /** Indicator whether the options are prepared. */
    protected boolean isPrepared = false;

    /**
     * Checks whether parameters are ok.
     * Returns non-zero error_code if parameters are not valid and help should be printed.
     * @return Returns non-zero error_code if invalid parameters
     */
    public abstract int checkParameters();

    /**
     * Executes the command.
     * @param options application options
     * @return exit code
     * @throws Exception if any error occurs
     */
    public int execute(final Options options) throws Exception {
        if (!isPrepared) {
            prepare(options);
            isPrepared = true;
        }
        return executeCommand(options);
    }

    /**
     * Executes the command.
     * @param options application options
     * @return exit code
     * @throws Exception if any error occurs
     */
    public abstract int executeCommand(Options options) throws Exception;

    /**
     * Prepares command's options.
     * Should throw ParameterException on error.
     * @param options
     */
    protected abstract void prepare(Options options);
}
