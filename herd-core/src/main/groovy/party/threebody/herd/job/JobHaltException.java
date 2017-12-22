package party.threebody.herd.job;

import party.threebody.skean.misc.SkeanException;

/**
 * an Exception that will cause Job halted and not go on any more.
 */
public class JobHaltException extends SkeanException {

    public JobHaltException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobHaltException(String message) {
        super(message);
    }
}
