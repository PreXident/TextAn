package cz.cuni.mff.ufal.textan.server.commands;


/**
 * The enum which specify a behavior of a command filtering in {@link cz.cuni.mff.ufal.textan.server.commands.CommandInvoker}.
 *
 * @author Petr Fanta
 */
public enum CommandFilterBehavior {
    /**
     * The filter is executed before the command.
     */
    BEFORE,
    /**
     * The filter is executed after the command.
     */
    AFTER,
    /**
     * The filter is not executed.
     */
    NONE
}
