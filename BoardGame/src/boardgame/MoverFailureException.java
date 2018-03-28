package boardgame;

/**
 * Exception thrown when for some reason a mover fails to choose a move.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public class MoverFailureException extends Exception {

    public MoverFailureException(String reason) {
        super(reason);
    }

    public MoverFailureException(Exception cause) {
        super(cause);
    }

}
