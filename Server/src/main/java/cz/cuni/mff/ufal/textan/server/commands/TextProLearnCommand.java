package cz.cuni.mff.ufal.textan.server.commands;

import cz.cuni.mff.ufal.textan.assigner.ITextPro;

/**
 * @author Petr Fanta
 */
public class TextProLearnCommand extends Command {

    private final ITextPro receiver;

    public TextProLearnCommand(ITextPro receiver) {
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

        TextProLearnCommand that = (TextProLearnCommand) o;

        return receiver.equals(that.receiver);
    }

    @Override
    public int hashCode() {
        return receiver.hashCode();
    }
}
