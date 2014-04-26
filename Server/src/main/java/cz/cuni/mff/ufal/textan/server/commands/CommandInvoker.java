package cz.cuni.mff.ufal.textan.server.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The thread-safe container (queue) for {@link Command}s,
 * which allows invocation of commands with a basic filtering of queue. *
 *
 * @author Petr Fanta
 */
public class CommandInvoker {

    private final Queue<Command> commandQueue = new LinkedList<>();
    private final Object queueLock = new Object();

    private boolean running = false;

    /**
     * Removes commands, which are equal to the command, from the command queue.
     *
     * @param command the model command
     * @see java.lang.Object#equals(Object)
     */
    private void filter(Command command) {

        synchronized (queueLock) {
            Iterator<Command> it = commandQueue.iterator();
            while (it.hasNext()) {
                if (command.equals(it.next())) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Invokes the head command of a command queue and performs filtering.
     *
     * @see cz.cuni.mff.ufal.textan.server.commands.CommandFilterBehavior
     */
    public void invoke() {

        Command command;

        synchronized (queueLock) {
            command = commandQueue.poll();
        }

        if (command != null) {

            if (command.getFilter() == CommandFilterBehavior.BEFORE) {
                filter(command);
            }

            command.execute();

            if (command.getFilter() == CommandFilterBehavior.AFTER) {
                filter(command);
            }
        }
    }

    /**
     * Adds the command to a command queue.
     *
     * @param command the command
     */
    public void register(Command command) {
        synchronized (queueLock) {
            commandQueue.add(command);
        }
    }

    /**
     * Returns the number of commands in this queue.
     *
     * @return the number of commands
     */
    public int size() {
        synchronized (queueLock) {
            return commandQueue.size();
        }
    }

    /**
     * Starts a continuous invocation of commands.
     * The method ends after {@link CommandInvoker#stop()} was called.
     */
    public void start() {
        running = true;

        while (running) {
            invoke();
            Thread.yield(); //TODO: in jetty is used Thread.sleep(1), what is better?
        }
    }

    /**
     * Stops a continuous invocation of commands.
     */
    public void stop() {
        running = false;
    }


    /**
     * Indicates if a continuous invocation is running.
     *
     * @return true if a continuous invocation is running, otherwise false
     */
    public boolean isRunning() {
        return running;
    }

}
