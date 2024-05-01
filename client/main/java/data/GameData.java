package data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;

import map.EField;
import map.EMapCharacter;
import map.FullMap;
import map.Position;

public class GameData {
	private final PropertyChangeSupport change = new PropertyChangeSupport(this);
	private String gameID;
	private String playerID;
	private LinkedHashMap<Position, EField> halfMap;
	private EPlayerState playerstate;
	private FullMap fullMap;
	private boolean treasureCollected = false;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		change.addPropertyChangeListener(listener);
	}

	public void updateGameID(String gameId) {
		String oldValue = this.gameID;
		this.gameID = gameId;

		change.firePropertyChange("gameID", oldValue, gameId);
	}

	public void updatePlayerID(String playerId) {
		String oldPlayerID = this.playerID;
		this.playerID = playerId;

		change.firePropertyChange("playerID", oldPlayerID, this.playerID);
	}

	public void updatePlayerState(EPlayerState newPlayerState) {
		EPlayerState oldState = this.playerstate;
		this.playerstate = newPlayerState;

		change.firePropertyChange("playerState", oldState, newPlayerState);
	}

	public void updatePlayerHalfMap(LinkedHashMap<Position, EField> playerHalfMap) {
		LinkedHashMap<Position, EField> oldMap = this.halfMap;
		this.halfMap = playerHalfMap;

		change.firePropertyChange("halfMap", oldMap, this.halfMap);
	}

	public void updateFullMap(FullMap newFullMap) {
		FullMap oldMap = this.fullMap;
		if (this.fullMap != null) {
			// null check because previous/old player positioons then can not be saved if
			// this is the first time the map is saved
			Position myNewPosition = newFullMap.getPlayerPosition(EMapCharacter.MY_PLAYER);
			Position myOldPosition = oldMap.getPlayerPosition(EMapCharacter.MY_PLAYER);
			Position enemyNewPosition = newFullMap.getPlayerPosition(EMapCharacter.ENEMY);
			Position enemyOldPosition = oldMap.getPlayerPosition(EMapCharacter.ENEMY);

			this.fullMap = newFullMap;

			if (!(myNewPosition.equals(myOldPosition)) || !(enemyNewPosition.equals(enemyOldPosition))) {
				change.firePropertyChange("fullMap", oldMap, this.fullMap);
			}
		} else {
			this.fullMap = newFullMap;
			change.firePropertyChange("fullMap", oldMap, this.fullMap);
		}
	}

	public void updateTreasureState(boolean foundTreasure) {
		boolean oldTreasureState = this.treasureCollected;
		this.treasureCollected = foundTreasure;

		change.firePropertyChange("treasureState", oldTreasureState, this.treasureCollected);
	}

	public EPlayerState getplayerState() {
		return this.playerstate;
	}

	public FullMap getFullMap() {
		return this.fullMap;
	}

}
