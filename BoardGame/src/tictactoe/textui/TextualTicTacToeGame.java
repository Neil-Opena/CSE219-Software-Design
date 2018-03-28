package tictactoe.textui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import tictactoe.TicTacToeBoard;
import tictactoe.TicTacToePlayer;

import boardgame.Game;
import boardgame.InteractiveMover;
import boardgame.Move;
import boardgame.Player;
import boardgame.RandomMover;

/**
 * Main class for the game of Tic-Tac-Toe. This version uses a textual user
 * interface.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public class TextualTicTacToeGame extends Game {

    /**
     * Where to get user input.
     */
    private final BufferedReader in;

    /**
     * Where to print output.
     */
    private final PrintWriter out;

    /**
     * Constructor
     *
     * @param board The game board.
     * @param in Where to get user input.
     * @param out Where to print output.
     */
    public TextualTicTacToeGame(TicTacToeBoard board, BufferedReader in, PrintWriter out) {
        super(board);
        this.in = in;
        this.out = out;
    }

    /**
     * Show the board to the user.
     */
    @Override
    public void showBoard() {
        getBoard().show();
    }

    /**
     * Show the current move to the user.
     *
     * @param move The move to be shown.
     */
    @Override
    public void showMove(Move move) {
        Player player = move.getWho();
        out.println("Player " + player.getName() + " plays: " + move);
    }

    /**
     * Show the winners of the game.
     */
    @Override
    public void showWinners() {
        for (Player p : getBoard().getPlayers()) {
            if (getBoard().isWinner(p)) {
                out.println("Player " + p.getName() + " has won.");
            }
        }
        out.flush();
    }

    /**
     * Let the user select which mark to play.
     */
    private void selectMark() throws IOException {
        TicTacToePlayer humanPlayer = null;
        TicTacToePlayer computerPlayer = null;
        TicTacToeBoard board = (TicTacToeBoard) getBoard();
        InteractiveMover.MoveParser parser = new TicTacToeMoveParser(board);
        while (humanPlayer == null) {
            out.print("Choose X or O: ");
            out.flush();
            String mark = in.readLine();
            if (mark != null) {
                mark = mark.trim().toUpperCase();
                if (mark.equals("X")) {
                    humanPlayer = new TicTacToePlayer("Human", TicTacToePlayer.X);
                    board.setPlayer(humanPlayer, TicTacToePlayer.X);
                    computerPlayer = new TicTacToePlayer("Computer", TicTacToePlayer.O);
                    board.setPlayer(computerPlayer, TicTacToePlayer.O);
                    break;
                } else if (mark.equals("O")) {
                    humanPlayer = new TicTacToePlayer("Human", TicTacToePlayer.O);
                    board.setPlayer(humanPlayer, TicTacToePlayer.O);
                    computerPlayer = new TicTacToePlayer("Computer", TicTacToePlayer.X);
                    board.setPlayer(computerPlayer, TicTacToePlayer.X);
                    break;
                } else {
                    out.println("'" + mark + "'" + " is not a valid selection.");
                }
            }
        }
        setMover(humanPlayer, new InteractiveMover(this, in, out, parser));
        setMover(computerPlayer, new RandomMover());
    }

    /**
     * Main method: starts the game.
     *
     * @param args Command-line arguments.
     * @throws IOException if an I/O error.
     */
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        TicTacToeBoard board
                = new TextualTicTacToeBoard(out);
        TextualTicTacToeGame game = new TextualTicTacToeGame(board, in, out);
        game.selectMark();
        game.play();
        out.flush();
    }
}
