package boardgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Mover that prompts the user at the console for a move.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public class InteractiveMover implements Mover {

    /**
     * The game to which this mover applies.
     */
    private final Game game;

    /**
     * Reader to use to read the user's input.
     */
    private final BufferedReader in;

    /**
     * Writer to use to prompt the user.
     */
    private final PrintWriter out;

    /**
     * Parser to use to interpret user input.
     */
    private final MoveParser parser;

    /**
     * Initialize a mover.
     *
     * @param game The game to which this mover applies.
     * @param in Where to read the user's input.
     * @param out Where to print the prompt.
     * @param parser Parser capable of interpreting user input as a move.
     */
    public InteractiveMover(Game game, BufferedReader in, PrintWriter out, MoveParser parser) {
        this.game = game;
        this.in = in;
        this.out = out;
        this.parser = parser;
    }

    /**
     * Prompt the user for a move and return the user's selection.
     *
     * @param player The player who is to move.
     * @param moves The set of available moves.
     * @return The chosen move.
     * @throws MoverFailureException if for some reason a move could not be
     * chosen.
     */
    @Override
    public Move chooseMove(Player player, Set<Move> moves) throws MoverFailureException {
        try {
            while (true) {
                game.showBoard();
                if (moves.isEmpty()) {
                    out.println(player.getName() + " has no legal moves.");
                    return null;
                } else if (moves.size() == 1) {
                    Move move = moves.iterator().next();
                    out.println(player.getName() + " has only one legal move: " + move);
                    return move;
                }
                out.print("Input move for " + player.getName() + ": ");
                out.flush();
                String s = in.readLine();
                if (s == null) {
                    throw new MoverFailureException("End of file on interactive input");
                }
                s = s.trim();
                if (s.equals("")) {
                    out.print("Do you want to quit (y/n)? ");
                    out.flush();
                    s = in.readLine();
                    if (s == null) {
                        throw new MoverFailureException("End of file on interactive input");
                    }
                    s = s.trim().toLowerCase();
                    if (s.startsWith("y")) {
                        throw new MoverFailureException("User requests to quit game");
                    } else {
                        continue;
                    }
                }
                Move move = parser.parseMove(player, s);
                if (move == null) {
                    out.println("Incomprehensible move.");
                    continue;
                }
                if (!moves.contains(move)) {
                    out.println(move + " is not a legal move.");
                    continue;
                }
                return move;
            }
        } catch (IOException x) {
            throw new MoverFailureException(x);
        }
    }

    /**
     * Interface implemented by objects that can interpret strings as moves.
     *
     * @author Eugene W. Stark
     * @date 20111021
     */
    public interface MoveParser {

        /**
         * Interpret a given string as a move.
         *
         * @param player The player making the move.
         * @param s The string to be interpreted as a move.
         * @return the corresponding move, or null if the string could not be
         * interpreted as specifying a move.
         */
        public Move parseMove(Player player, String s);

    }
}
