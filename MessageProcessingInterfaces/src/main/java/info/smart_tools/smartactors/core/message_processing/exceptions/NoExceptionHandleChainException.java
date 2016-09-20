package info.smart_tools.smartactors.core.message_processing.exceptions;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;

import java.text.MessageFormat;

/**
 * Exception thrown by {@link
 * info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence#catchException(Throwable, IObject)}
 * when no one of {@link IReceiverChain}'s in the stack defines an
 * exceptional chain for occurred exception.
 */
public class NoExceptionHandleChainException extends Exception {
    private final IReceiverChain[] chainsStack;
    private final int[] stepsStack;

    /**
     * The constructor.
     *
     * @param cause          the occurred exception no chain to handle found for
     * @param chainsStack    stack of chains where found no exceptional chain
     * @param stepsStack     steps in chains of stack where exception occurred
     */
    public NoExceptionHandleChainException(final Throwable cause, final IReceiverChain[] chainsStack, final int[] stepsStack) {
        super(
                MessageFormat.format("No exceptional chain found for exception occurred at step {0} of chain ''{1}''.",
                        (stepsStack.length != 0) ? stepsStack[stepsStack.length - 1] : "<none>",
                        (chainsStack.length != 0) ? chainsStack[chainsStack.length - 1].getName() : "<none>"),
                cause);

        this.chainsStack = chainsStack;
        this.stepsStack = stepsStack;
    }

    public IReceiverChain[] getChainsStack() {
        return chainsStack;
    }

    public int[] getStepsStack() {
        return stepsStack;
    }
}
