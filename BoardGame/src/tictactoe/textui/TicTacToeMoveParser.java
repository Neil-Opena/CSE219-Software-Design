package tictactoe.textui;

import tictactoe.TicTacToeBoard;
import tictactoe.TicTacToeMove;
import tictactoe.TicTacToePlayer;
import tictactoe.TicTacToeBoard.Location;
import boardgame.Move;
import boardgame.InteractiveMover;
import boardgame.Player;

/**
 * A move parser that can parse moves for the game of TicTacToe.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public class TicTacToeMoveParser implements InteractiveMover.MoveParser {

    /**
     * The board to which the parsed moves are to apply.
     */
    private TicTacToeBoard board;

    /**
     * Initialize a move parser for a specified board.
     *
     * @param board The board.
     */
    public TicTacToeMoveParser(TicTacToeBoard board) {
        this.board = board;
    }

    @Override
    public Move parseMove(Player player, String s) {
        if (s == null) {
            return null;
        }
        if (!s.startsWith("(")) {
            return null;
        }
        s = s.substring(1);
        if (!s.endsWith(")")) {
            return null;
        }
        s = s.substring(0, s.length() - 1);
        String[] fields = s.split(",");
        if (fields == null || fields.length != 2) {
            return null;
        }
        try {
            int row = Integer.parseInt(fields[0]);
            int col = Integer.parseInt(fields[1]);
            Location loc = board.new Location(row, col);
            Move move = new TicTacToeMove((TicTacToePlayer) player, board, loc);
            return move;
        } catch (NumberFormatException x) {
            return null;
        } catch (IllegalArgumentException x) {
            return null;
        }
    }

}
