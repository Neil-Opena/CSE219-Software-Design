package boardgame;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class whose objects represent a generic board game.
 *
 * @author Eugene W. Stark
 * @version 20111022
 */
public class Game {

    /**
     * The game board.
     */
    private final Board board;

    /**
     * Map that gives the mover to use for each player.
     */
    private final Map<Player, Mover> playerMover;

    /**
     * Initialize a game.
     *
     * @param board The game board.
     */
    public Game(Board board) {
        this.board = board;
        playerMover = new HashMap<>();
    }

    /**
     * Get the game board.
     *
     * @return the game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Show the game board. The default implementation does nothing. Subclasses
     * may override this in order to cause the board to be displayed for the
     * user.
     */
    public void showBoard() {
    }

    /**
     * Show the current move. The default implementation does nothing.
     * Subclasses may override this in order to cause the move to be displayed
     * for the user.
     *
     * @param move The move to be shown.
     */
    public void showMove(Move move) {
    }

    /**
     * Show the winners of the game. The default implementation does nothing.
     * Subclasses may override this in order to cause the winners to be
     * displayed for the user.
     */
    public void showWinners() {
    }

    /**
     * Set the mover for a player.
     *
     * @param player The player.
     * @param mover The mover to use for that player.
     */
    public void setMover(Player player, Mover mover) {
        playerMover.put(player, mover);
    }

    /**
     * Play the game.
     */
    public void play() {
        while (!board.gameIsOver()) {
            Player player = board.playerToMove();
            Mover mover = playerMover.get(player);
            if (mover == null) {
                mover = new RandomMover();
                playerMover.put(player, mover);
            }
            try {
                Move move = mover.chooseMove(player, board.getLegalMoves());
                showMove(move);
                board.apply(move);
            } catch (MoverFailureException x) {
                break;
            }
        }
        if (board.gameIsOver()) {
            showBoard();
            showWinners();
        }
    }

}
