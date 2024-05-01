package map;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exceptions.InvalidMapCharacterPosition;

public class FullMap {
	private LinkedHashMap<Position, EField> fullMap = new LinkedHashMap<>();
	private LinkedHashMap<EMapCharacter, Position> playerPositions = new LinkedHashMap<>();
	private LinkedHashMap<EMapCharacter, Position> castlePositions = new LinkedHashMap<>();
	private Position treasurePosition;

	Logger logger = LoggerFactory.getLogger(FullMap.class);

	public FullMap(LinkedHashMap<Position, EField> baseFullMap) {

		if (baseFullMap.isEmpty()) {
			logger.error("Invalid base Map, should not be empty - check server side map");
		}

		this.fullMap = baseFullMap;
	}

	public LinkedHashMap<Position, EField> getBaseMap() {
		return this.fullMap;
	}

	public void placePlayerOnMap(EMapCharacter playerType, Position playerPosition) throws InvalidMapCharacterPosition {
		if (!hasPosition(playerPosition)) {
			throw new InvalidMapCharacterPosition(playerType, playerPosition);
		} else {
			playerPositions.put(playerType, playerPosition);
		}
	}

	public Position getPlayerPosition(EMapCharacter playerType) {
		return playerPositions.get(playerType);
	}

	public void placeCastleOnMap(EMapCharacter playerType, Position position) throws InvalidMapCharacterPosition {
		if (!hasPosition(position)) {
			throw new InvalidMapCharacterPosition("Map Characters castle is out of the map.");
		}
		castlePositions.put(playerType, position);
	}

	public Position getCastlePosition(EMapCharacter playerType) {
		return castlePositions.get(playerType);
	}

	public void placeTreasure(Position position) {
		this.treasurePosition = position;
	}

	public Position getTreasurePosition() {
		return this.treasurePosition;
	}

	public boolean hasPosition(Position search) {
		boolean contains = false;
		for (Map.Entry<Position, EField> entry : fullMap.entrySet()) {
			Position comparator = entry.getKey();
			if (comparator.getXCoordinate() == search.getXCoordinate()
					&& comparator.getYCoordinate() == search.getYCoordinate()) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public EField getPositionField(Position search) {
		EField field = EField.GRASS;
		for (Map.Entry<Position, EField> entry : fullMap.entrySet()) {
			Position comparator = entry.getKey();
			if (comparator.getXCoordinate() == search.getXCoordinate()
					&& comparator.getYCoordinate() == search.getYCoordinate()) {
				field = entry.getValue();
				break;
			}
		}
		return field;
	}

	public boolean checkIfMapHotizontal() {
		boolean isHorizontal = false;
		final int verticalMapXMaximum = 9;
		for (Map.Entry<Position, EField> mapField : fullMap.entrySet()) {
			if (mapField.getKey().getXCoordinate() > verticalMapXMaximum) {
				isHorizontal = true;
				break;
			}
		}
		return isHorizontal;
	}
}
