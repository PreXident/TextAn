package cz.cuni.mff.ufal.textan.server.commands;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CommandInvokerTest {

    CommandInvoker invoker;
    Receiver receiver;

    @Before
    public void setUp() throws Exception {
        invoker = new CommandInvoker();
        receiver = mock(Receiver.class);
    }

    @Test
    public void testInvoke() {

        TestCommand testCommand = new TestCommand(receiver, CommandFilterBehavior.NONE);
        invoker.register(testCommand);

        invoker.invoke();

        verify(receiver, times(1)).action();
    }

    @Test
    public void testInvokeWithFilterNone() {

        int n = 3;
        for (int i = 0; i < n; ++i) {
            invoker.register(new TestCommand(receiver, CommandFilterBehavior.NONE));
        }

        for (int i = 0; i < n; ++i) {
            invoker.invoke();
        }

        verify(receiver, times(n)).action();
    }

    @Test
    public void testInvokeWithFilterBefore() {

        int n = 3;
        for (int i = 0; i < n; ++i) {
            invoker.register(new TestCommand(receiver, CommandFilterBehavior.BEFORE));
        }

        for (int i = 0; i < n; ++i) {
            invoker.invoke();
        }

        verify(receiver, times(1)).action();
    }

    @Test
    public void testInvokeWithFilterAfter() {

        int n = 3;
        for (int i = 0; i < n; ++i) {
            invoker.register(new TestCommand(receiver, CommandFilterBehavior.AFTER));
        }

        for (int i = 0; i < n; ++i) {
            invoker.invoke();
        }

        verify(receiver, times(1)).action();
    }

    @Test
    public void testRegister() {

        int sizeBefore = invoker.size();
        invoker.register(new TestCommand(null, CommandFilterBehavior.NONE));
        int sizeAfter = invoker.size();

        Assert.assertTrue(sizeBefore + 1 == sizeAfter);
    }

    interface Receiver {
        public void action();
    }

    class TestCommand extends Command {

        Receiver receiver;

        public TestCommand(Receiver receiver, CommandFilterBehavior filter) {
            super(filter);
            this.receiver = receiver;
        }

        @Override
        public void execute() {
            receiver.action();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            TestCommand testCommand = (TestCommand) obj;

            return receiver == testCommand.receiver;
        }

        @Override
        public int hashCode() {
            return receiver != null ? receiver.hashCode() : 0;
        }
    }
}