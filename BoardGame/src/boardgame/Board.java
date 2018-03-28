package boardgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract base class whose objects represent the state of a board during the
 * play of a board game.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public abstract class Board {

    /**
     * Set of the legal moves from this board state, or null if the set has not
     * yet been generated.
     */
    private Set<Move> legalMoves;

    /**
     * List of moves that have been made since the beginning of the game.
     */
    private List<Move> gameHistory;

    /**
     * List that provides information necessary to undo the effect of the moves
     * in the game history.
     */
    private List<Move.UndoInfo> undoList;

    /**
     * Initialize a board.
     */
    protected Board() {
        gameHistory = new ArrayList<>();
        undoList = new ArrayList<>();
    }

    /**
     * Reset the state of the board to the beginning of the game.
     */
    public void reset() {
        gameHistory = new ArrayList<>();
        undoList = new ArrayList<>();
        legalMoves = null;
        resetBoard();
    }

    /**
     * Reset the game-specific state of the board to the beginning of the game.
     * Subclasses must override this.
     */
    protected abstract void resetBoard();

    /**
     * Get a list of the players in the game, in the order in which they are to
     * move.
     *
     * @return a list of the players in the game.
     */
    public abstract List<Player> getPlayers();

    /**
     * Show the current state of the board. The default implementation does
     * nothing. Subclasses may override this to cause the board to be displayed.
     */
    public void show() {
    }

    /**
     * Determine if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public abstract boolean gameIsOver();

    /**
     * Determine if a specified player has won the game. In some games it may be
     * possible for more than one player to win.
     *
     * @param player The player.
     * @return true if the specified player has won the game, false otherwise.
     */
    public abstract boolean isWinner(Player player);

    /**
     * Get the set of legal moves for the current state of this board.
     *
     * @return the list of legal moves for the current state of this board.
     */
    public Set<Move> getLegalMoves() {
        if (legalMoves == null) {
            legalMoves = generateLegalMoves();
        }
        return legalMoves;
    }

    /**
     * Generate a set of the legal moves for the current state of this board.
     *
     * @return the set of legal moves.
     */
    protected abstract Set<Move> generateLegalMoves();

    /**
     * Determine if a move is legal in the current board position.
     *
     * @param move the move to be checked for legality.
     * @return true if the move is legal, false otherwise.
     */
    public boolean isLegalMove(Move move) {
        return getLegalMoves().contains(move);
    }

    /**
     * Apply a specified move to the board. The board state is updated and the
     * move is recorded in the game history.
     *
     * @param move The move to be applied to the board.
     * @throw IllegalStateException if the specified move is not for this board,
     * or is not a legal move in the current board state.
     */
    public void apply(Move move) {
        if (!this.equals(move.getBoard())) {
            throw new IllegalStateException("Move is not for this board");
        }
        boolean legal = false;
        for (Move legalMove : getLegalMoves()) {
            if (move.equals(legalMove)) {
                legal = true;
                break;
            }
        }
        if (!legal) {
            throw new IllegalStateException("Move is not legal for the current board state");
        }
        Move.UndoInfo undo = applyMove(move);
        gameHistory.add(move);
        undoList.add(undo);
        legalMoves = null;
    }

    /**
     * Apply a specified move to the board, updating the board state. Subclasses
     * must override this method. It can be assumed that the move is a legal
     * move that applies to the current board.
     *
     * @param move The move to be applied to the board.
     * @return An object that describes how to undo the effect of the move.
     */
    protected abstract Move.UndoInfo applyMove(Move move);

    /**
     * Undo the last move applied to the board. The board state and game history
     * are updated.
     *
     * @throw IllegalStateException if the game history is empty.
     */
    public void undo() {
        if (gameHistory.isEmpty()) {
            throw new IllegalStateException("There is no move to undo");
        }
        Move.UndoInfo undo = undoList.remove(undoList.size() - 1);
        gameHistory.remove(gameHistory.size() - 1);
        undoMove(undo);
        legalMoves = null;
    }

    /**
     * Apply the specified undo information to undo the effect of a move.
     * Subclasses must override this method. It can be assumed that the undo
     * information corresponds to the last move that was applied to the board.
     *
     * @param undo Object that provides information necessary to undo the move.
     */
    protected abstract void undoMove(Move.UndoInfo undo);

    /**
     * Find out which player is on the move.
     *
     * @return the player whose move it is, or null if no player can move.
     */
    public abstract Player playerToMove();

}
