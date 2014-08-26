package cz.cuni.mff.ufal.textan.server.commands;

import cz.cuni.mff.ufal.textan.assigner.IObjectAssigner;

/**
 * @author Petr Fanta
 */
public class ObjectAssignerLearnCommand extends Command {

    private final IObjectAssigner receiver;

    public ObjectAssignerLearnCommand(IObjectAssigner receiver) {
        super(CommandFilterBehavior.BEFORE);
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.learn();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectAssignerLearnCommand that = (ObjectAssignerLearnCommand) o;

        return receiver.equals(that.receiver);
    }

    @Override
    public int hashCode() {
        return receiver.hashCode();
    }
}
