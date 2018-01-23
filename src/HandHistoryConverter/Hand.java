package HandHistoryConverter;

public class Hand {

	private String time;
	private String ID;
	private String website;
	private String stakes;
	private String limit;
	private double pot;
	private int buttonPosition;
	private String action;

	public Hand(String time, String iD, String website, String stakes, String limit, double pot, int buttonPosition,
			String action) {
		super();
		this.time = time;
		ID = iD;
		this.website = website;
		this.stakes = stakes;
		this.limit = limit;
		this.pot = pot;
		this.buttonPosition = buttonPosition;
		this.action = action;

	}

	public Hand(String time, String website, String stakes, String limit, double pot, int buttonPosition,
			String action) {
		super();
		this.time = time;
		this.website = website;
		this.stakes = stakes;
		this.limit = limit;
		this.pot = pot;
		this.buttonPosition = buttonPosition;
		this.action = action;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStakes() {
		return stakes;
	}

	public void setStakes(String stakes) {
		this.stakes = stakes;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public double getPot() {
		return pot;
	}

	public void setPot(double pot) {
		this.pot = pot;
	}

	public int getButtonPosition() {
		return buttonPosition;
	}

	public void setButtonPosition(int buttonPosition) {
		this.buttonPosition = buttonPosition;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
