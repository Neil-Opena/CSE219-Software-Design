package tictactoe;

import boardgame.Move;

/**
 * Class used to represent a TicTacToe move.
 *
 * @author Eugene W. Stark
 * @version 200501
 * @version 20111021
 */
public class TicTacToeMove extends Move {

    /**
     * The location for this move.
     */
    private final TicTacToeBoard.Location location;

    /**
     * Initialize a move.
     *
     * @param who The player who is making the move.
     * @param board The board to which the move applies.
     * @param loc The location of the move.
     */
    public TicTacToeMove(TicTacToePlayer who, TicTacToeBoard board, TicTacToeBoard.Location loc) {
        super(who, board);
        location = loc;
    }

    /**
     * Obtain the board location of this move.
     *
     * @return the board location of this move.
     */
    public TicTacToeBoard.Location getLocation() {
        return location;
    }

    /**
     * Create a printable representation of this move.
     *
     * @return a printable representation of this move.
     */
    @Override
    public String toString() {
        return location.toString();
    }

    @Override
    public boolean equals(Object o){
	    if(o == this){
		return true;
	    }else if(o == null){ 
		return false;
	    }else if(!o.getClass().equals(this.getClass())){
		return false;
	    }

	    TicTacToeMove move = (TicTacToeMove) o;
	    return this.location.equals(move.location);
    }

    @Override
    public int hashCode(){
	return this.location.hashCode();
    }

}
