package boardgame;

import java.util.Set;

/**
 * Interface implemented by objects that know how to choose a move from among a
 * list of legal moves for a given board.
 *
 * @author Eugene W. Stark
 * @version 20111021
 */
public interface Mover {

    /**
     * Choose a move to play from a given set of alternative moves.
     *
     * @param player The player who is to move.
     * @param moves Set of alternative moves.
     * @return the chosen move.
     * @throws MoverFailureException if for some reason the mover fails to
     * choose a move.
     */
    public Move chooseMove(Player player, Set<Move> moves) throws MoverFailureException;

}
