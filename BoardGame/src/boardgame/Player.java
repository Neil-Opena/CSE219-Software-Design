package boardgame;

/**
 * Abstract base class whose objects represent players in a board game.
 * 
 * @author Eugene W. Stark
 * @version 20111021
 */
public abstract class Player {
	
	/** The player's name. */
	private final String name;
	
	/**
	 * Initialize a player with a specified name.
	 * 
	 * @param name  The player's name.
	 */
	protected Player(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of this player.
	 * 
	 * @return the player's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Create a string representation of this player.
	 * 
	 * @return a string representation of this player.
	 */
        @Override
	public String toString() {
		return this.getClass().getName() + "[" + getName() + "]";
	}

}
