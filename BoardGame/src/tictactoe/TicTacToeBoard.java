package tictactoe;

import java.util.ArrayList;
import java.util.List;

import boardgame.Board;
import boardgame.Move;
import boardgame.Move.UndoInfo;
import boardgame.Player;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a TicTacToe board.
 *
 * @author Eugene W. Stark
 * @version 200501
 * @version 20111021
 */
public class TicTacToeBoard extends Board {

    /**
     * Object representing the 'X' player.
     */
    private TicTacToePlayer X;

    /**
     * Object representing the 'O' player.
     */
    private TicTacToePlayer O;

    /**
     * Constant value defining the size of the board.
     */
    private static final int BOARD_SIZE = 3;

    /**
     * 2-D array representing the board contents. An entry is null if no player
     * has yet taken that square, otherwise the entry records the player who
     * "owns" the square.
     */
    private TicTacToePlayer[][] theBoard;

    /**
     * The player on the move.
     */
    private int whoseMove;

    /**
     * Constructor for objects of class Board
     */
    public TicTacToeBoard() {
        super();
        theBoard = new TicTacToePlayer[BOARD_SIZE][BOARD_SIZE];
        whoseMove = TicTacToePlayer.X;
    }

    /**
     * Set a player to be either X or O.
     *
     * @param player The player.
     * @param which Either TicTacToePlayer.X or TicTacToePlayer.O.
     */
    public void setPlayer(TicTacToePlayer player, int which) {
        if (which == TicTacToePlayer.X) {
            X = player;
        } else if (which == TicTacToePlayer.O) {
            O = player;
        }
    }

    /**
     * Get a list of the players in the game, in the order in which they are to
     * move.
     *
     * @return a list of the players in the game.
     */
    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(X);
        players.add(O);
        return players;
    }

    /**
     * Reset the board state to the beginning of the game.
     */
    @Override
    public void resetBoard() {
        theBoard = new TicTacToePlayer[BOARD_SIZE][BOARD_SIZE];
        whoseMove = TicTacToePlayer.X;
    }

    /**
     * Determine whose move it is.
     *
     * @return the player who is on the move.
     */
    @Override
    public TicTacToePlayer playerToMove() {
        return (whoseMove == TicTacToePlayer.X ? X : O);
    }

    /**
     * Determine whether the game is over.
     *
     * @return true if the game is over, otherwise false.
     */
    @Override
    public boolean gameIsOver() {
        if (getLegalMoves().isEmpty()) {
            return (true);
        }
        if (getWinner() != null) {
            return (true);
        }
        return (false);
    }

    /**
     * Internal method used to check for three-in a row, column, or diagonal.
     * From a specified starting row and column, traverse in a specified
     * direction, accumulating a sum. Each time X is encountered, BOARD_SIZE+1
     * is added. Each time O is encountered, 1 is added. The resulting sum has
     * the property that sum/(BOARD_SIZE+1) is the number of X's encountered,
     * and sum%(BOARD_SIZE+1) is the number of O's encountered.
     *
     * @param startRow The starting row position.
     * @param startCol The starting column position.
     * @param rowInc The increment to apply to the row position.
     * @param colInc The increment to apply to the column position.
     * @return An integer "sum" having the property that sum/(BOARD_SIZE+1) is
     * the number of X's seen and sum%(BOARD_SIZE+1) is the number of O's seen.
     */
    private int calcSum(int startRow, int startCol, int rowInc, int colInc) {
        int sum = 0;
        for (int row = startRow, col = startCol;
                row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
                row += rowInc, col += colInc) {
            Player who = theBoard[row][col];
            if (who == X) {
                sum += BOARD_SIZE + 1;
            } else if (who == O) {
                sum += 1;
            }
        }
        return (sum);
    }

    /**
     * Internal method used to check for a winner in a single direction.
     *
     * @param startRow The starting row position.
     * @param startCol The starting column position.
     * @param rowInc The increment to apply to the row position.
     * @param colInc The increment to apply to the column position.
     * @return If X has won, return X, and if O has won, return O, otherwise
     * return null.
     */
    private Player checkDirection(int startRow, int startCol, int rowInc, int colInc) {
        int sum = calcSum(startRow, startCol, rowInc, colInc);
        if (sum / (BOARD_SIZE + 1) >= BOARD_SIZE) {
            return (X);
        } else if (sum % (BOARD_SIZE + 1) >= BOARD_SIZE) {
            return (O);
        }
        return (null);
    }

    /**
     * Figure out who has won the game.
     *
     * @return the player who has won, or null if no one has won.
     */
    private Player getWinner() {
        Player who;

        // First check for three in a row.
        for (int row = 0; row < BOARD_SIZE; row++) {
            who = checkDirection(row, 0, 0, 1);
            if (who != null) {
                return (who);
            }
        }

        // Next check for three in a column.
        for (int col = 0; col < BOARD_SIZE; col++) {
            who = checkDirection(0, col, 1, 0);
            if (who != null) {
                return (who);
            }
        }

        // Finally, check the diagonals.
        who = checkDirection(0, 0, 1, 1);
        if (who != null) {
            return (who);
        }
        who = checkDirection(BOARD_SIZE - 1, 0, -1, 1);
        if (who != null) {
            return (who);
        }

        return (null);
    }

    /**
     * Determine if a specified player has won the game. In some games it may be
     * possible for more than one player to win.
     *
     * @param player The player.
     * @return true if the specified player has won the game, false otherwise.
     */
    @Override
    public boolean isWinner(Player player) {
        return player == getWinner();
    }

    /**
     * Obtain a list of the legal moves in the current board position.
     *
     * @return a list containing the legal moves in the current board position.
     */
    @Override
    protected Set<Move> generateLegalMoves() {
        // First, compute all the legal moves and add them to an ArrayList.
        Set<Move> a = new HashSet<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (theBoard[row][col] == null) {
                    a.add(new TicTacToeMove(playerToMove(), this, new Location(row, col)));
                }
            }
        }
        return a;
    }

    /**
     * Apply a specified move to the board.
     *
     * @param move The move to be made.
     * @return An object that provides information necessary to undo the move.
     */
    @Override
    protected UndoInfo applyMove(Move move) {
        Move.UndoInfo undo = move.new UndoInfo();
        TicTacToeMove tMove = (TicTacToeMove) move;
        if (isLegalMove(move)) {
            Location loc = tMove.getLocation();
            int row = loc.getRow();
            int col = loc.getColumn();
            theBoard[row][col] = playerToMove();
            if (whoseMove == TicTacToePlayer.X) {
                whoseMove = TicTacToePlayer.O;
            } else {
                whoseMove = TicTacToePlayer.X;
            }
        }
        return undo;
    }

    /**
     * Apply the specified undo information to undo the effect of a move.
     * Subclasses must override this method. It can be assumed that the undo
     * information corresponds to the last move that was applied to the board.
     *
     * @param undo Object that provides information necessary to undo the move.
     */
    @Override
    protected void undoMove(TicTacToeMove.UndoInfo undo) {
        TicTacToeMove move = (TicTacToeMove) undo.getMove();
        Location loc = move.getLocation();
        int row = loc.getRow();
        int col = loc.getColumn();
        if (whoseMove == TicTacToePlayer.X) {
            whoseMove = TicTacToePlayer.O;
        } else {
            whoseMove = TicTacToePlayer.X;
        }
        theBoard[row][col] = null;
    }

    /**
     * Create a printable representation of a board.
     *
     * @return a string giving a printable representation of the board.
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Player player = theBoard[i][j];
                if (player == null) {
                    result = result + " ";
                } else if (player == X) {
                    result = result + "X";
                } else if (player == O) {
                    result = result + "O";
                }
                if (j < BOARD_SIZE - 1) {
                    result = result + "|";
                }
            }
            result = result + "\n";
            if (i < BOARD_SIZE - 1) {
                result = result + "-----\n";
            }
        }
        return result;
    }

    /**
     * Objects of this class represent locations on a tic-tac-toe board.
     *
     * @author Eugene W. Stark
     * @date 20111021
     */
    public class Location {

        /**
         * The number of this location. Locations are numbered starting from 0
         * at the top left square, increasing across a row and then to the
         * leftmost square of the next row.
         */
        private int number;

        public Location(int row, int col) {
            if ((row < 0 || row >= BOARD_SIZE) || (col < 0 || col >= BOARD_SIZE)) {
                throw new IllegalArgumentException("Row or column number out-of-range");
            }
            number = locationNumber(row, col);
        }

        /**
         * Convert a (row, col) pair into a location number.
         *
         * @param row The row number.
         * @param col The column number.
         * @return The location number corresponding to the specified row and
         * column numbers.
         */
        private int locationNumber(int row, int col) {
            return (BOARD_SIZE * row) + col;
        }

        /**
         * Get the row number of this location.
         *
         * @return the row number of this location.
         */
        public int getRow() {
            return number / BOARD_SIZE;
        }

        /**
         * Get the column number of this location.
         *
         * @return the column number of this location.
         */
        public int getColumn() {
            return number % BOARD_SIZE;
        }

        /**
         * Obtain a string representation of this location.
         *
         * @return a string representation of this location.
         */
        @Override
        public String toString() {
            return "(" + getRow() + "," + getColumn() + ")";
        }

        /**
         * Determine whether this location is the same as a specified other
         * location.
         *
         * @param other The location to be compared to this location.
         * @return true if this location is the same as the other location,
         * otherwise false.
         */
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }
            Location otherLocation = (Location) other;
            return number == otherLocation.number;
        }

    }
}
