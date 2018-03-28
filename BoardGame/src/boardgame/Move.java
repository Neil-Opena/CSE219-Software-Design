package boardgame;

/**
 * Abstract base class whose objects represent moves in a board game.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public abstract class Move {

    /**
     * The player who is making this move.
     */
    private final Player who;

    /**
     * The board to which this move applies.
     */
    private final Board board;

    /**
     * Initialize a move for a specified player.
     *
     * @param board The board to which this move applies.
     * @param who The player who is making this move.
     */
    protected Move(Player who, Board board) {
        this.who = who;
        this.board = board;
    }

    /**
     * Get the player who is making this move.
     *
     * @return the player who is making this move.
     */
    public Player getWho() {
        return who;
    }

    /**
     * Get the board to which this move applies.
     *
     * @return the board to which this move applies.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Obtain a string representation of this move.
     *
     * @return a string representation of this move.
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + getWho() + "]";
    }

    /**
     * Base class whose objects contain state information necessary in order to
     * undo the effect of this move. The default implementation records no
     * information and provides no methods. Subclasses can override this with
     * game-specific methods and state information.
     */
    public class UndoInfo {

        /**
         * Get the move to which this undo information applies.
         *
         * @return the move to which this undo information applies.
         */
        public Move getMove() {
            return Move.this;
        }
    }

}
