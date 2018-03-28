package tictactoe.textui;

import java.io.PrintWriter;

import tictactoe.TicTacToeBoard;

/**
 * TicTacToe board that displays as text.
 *
 * @author Eugene W. Stark
 * @version 20111021
 *
 */
public class TextualTicTacToeBoard extends TicTacToeBoard {

    /**
     * Writer to be used to show the board.
     */
    PrintWriter out;

    public TextualTicTacToeBoard(PrintWriter out) {
        super();
        this.out = out;
    }

    @Override
    public void show() {
        out.println();
        out.print(toString());
        out.println();
        out.flush();
    }

}
