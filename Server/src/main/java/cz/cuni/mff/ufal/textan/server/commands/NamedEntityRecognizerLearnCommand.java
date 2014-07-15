package cz.cuni.mff.ufal.textan.server.commands;

import cz.cuni.mff.ufal.textan.server.linguistics.NamedEntityRecognizer;

/**
 * @author Petr Fanta
 */
public class NamedEntityRecognizerLearnCommand extends Command {

    private final NamedEntityRecognizer receiver;

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

        return receiver == command.receiver;
        //return !(receiver != null ? !receiver.equals(that.receiver) : that.receiver != null);
    }
}
