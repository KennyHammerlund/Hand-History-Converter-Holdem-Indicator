package HandHistoryConverter;

public class Player {
	private Object holeCards;
	private double stack;
	private String name;

	/**
	 * Creates Player
	 * 
	 * @param name
	 *            Name of player
	 * @param stack
	 *            Amount player has
	 * @param holeCards
	 *            format: [Ac As]
	 */
	public Player(String name, double stack, String holeCards) {
		this.name = name;
		this.stack = stack;
		this.holeCards = holeCards;

	}
}
