package game.data;

public class PlayerInformation {
	private String firstName;
	private String lastName;
	private String uaccount;
	private boolean collectedTreasure = false;
	private boolean sentMapHalf = false;
	private EPlayerTurn playerTurnState = EPlayerTurn.WAIT_FOR_TURN;

	public PlayerInformation(String firstName, String lastName, String uaccount) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
	}

	public boolean getTreasureState() {
		return collectedTreasure;
	}

	public void pickUpTreasure() {
		collectedTreasure = true;
	}

	public void sentMapHalf() {
		sentMapHalf = true;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUaccount() {
		return uaccount;
	}

	public boolean isMapHalfSent() {
		return sentMapHalf;
	}

	public EPlayerTurn getPlayerTurnState() {
		return playerTurnState;
	}

	public void setPlayerTurnState(EPlayerTurn playerTurnState) {
		this.playerTurnState = playerTurnState;
	}

}
