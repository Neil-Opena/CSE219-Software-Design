package tictactoe;

import boardgame.Player;

/**
 * Extension of Player whose objects represent players in the game of TicTacToe.
 *
 * @author Eugene Stark
 * @date 20111022
 *
 */
public class TicTacToePlayer extends Player {

    public static final int X = 0;
    public static final int O = 1;

    private final int which;

    /**
     * Initialize a player with a specified name.
     *
     * @param name The player's name.
     * @param which Either X or O, depending on whether the player is the first
     * or second to move.
     */
    public TicTacToePlayer(String name, int which) {
        super(name);
        this.which = which;
    }

    /**
     * Determine whether this player is X or O.
     *
     * @return X or O, depending on whether this player is the first or second
     * to move.
     */
    public int getWhich() {
        return which;
    }

    /**
     * Get the name of this player.
     *
     * @return the name of this player.
     */
    @Override
    public String getName() {
        return super.getName() + "(" + (which == X ? "X" : "O") + ")";
    }

}
