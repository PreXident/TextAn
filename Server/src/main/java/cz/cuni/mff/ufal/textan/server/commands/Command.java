package cz.cuni.mff.ufal.textan.server.commands;

/**
 * The base class for a commands.
 *
 * @author Petr Fanta
 */
public abstract class Command {

    private final CommandFilterBehavior filter;

    /**
     * Instantiates a new Command.
     *
     * @param filter the behavior of filter in {@link cz.cuni.mff.ufal.textan.server.commands.CommandInvoker}
     */
    protected Command(CommandFilterBehavior filter) {
        this.filter = filter;
    }

    CommandFilterBehavior getFilter() {
        return filter;
    }

    /**
     * Executes an operation on a receiver.
     */
    public abstract void execute();
}
