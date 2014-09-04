package cz.cuni.mff.ufal.textan.server.commands;

import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;

/**
 * A command for invoking {@link cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer#learn(boolean)}.
 * The parameter of learn method is pressed to {@code true}
 *
 * @author Petr Fanta
 */
public class NamedEntityRecognizerLearnCommand extends Command {

    private final NamedEntityRecognizer receiver;

    /**
     * Creates a learn command for given {@link cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer}
     * @param receiver the instance of {@link cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer}
     */
    public NamedEntityRecognizerLearnCommand(NamedEntityRecognizer receiver) {
        super(CommandFilterBehavior.BEFORE);
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.learn(true);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        NamedEntityRecognizerLearnCommand command = (NamedEntityRecognizerLearnCommand) obj;

        return receiver.equals(command.receiver);
        //return !(receiver != null ? !receiver.equals(that.receiver) : that.receiver != null);
    }

    @Override
    public int hashCode() {
        return receiver.hashCode();
    }
}
