package info.smart_tools.smartactors.base.interfaces.ipool.exception;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;

/**
 * Exception for error in {@link IPool} method take
 */
public class PoolTakeException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PoolTakeException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PoolTakeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PoolTakeException(final Throwable cause) {
        super(cause);
    }
}