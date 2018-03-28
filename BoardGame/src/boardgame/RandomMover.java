package boardgame;

import java.util.Random;
import java.util.Set;

/**
 * Mover that chooses a random move from among the available legal moves.
 *
 * @author Eugene W. Stark
 * @version 20111021
 *
 */
public class RandomMover implements Mover {

    /**
     * Source of random numbers.
     */
    private final Random random = new Random();

    /**
     * Choose a random move to play from a given set of alternative moves.
     *
     * @param player The player who is to move.
     * @param moves Set of alternative moves.
     * @return the chosen move.
     */
    @Override
    public Move chooseMove(Player player, Set<Move> moves) {
        int i = random.nextInt(moves.size());
        for (Move move : moves) {
            if (i-- == 0) {
                return move;
            }
        }
        return null;  // Cannot happen.
    }

}
