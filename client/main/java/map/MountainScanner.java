package map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MountainScanner {
	private final Logger logger = LoggerFactory.getLogger(MountainScanner.class);

	public List<Position> getSurroundingGrassFields(FullMap fullMap, Position currentPosition) {
		List<Position> surroundingGrassFields = new ArrayList<>();
		LinkedHashMap<Position, EField> baseMap = fullMap.getBaseMap();
		List<Position> surroundingFields = getSurroundingCoordinates(currentPosition);

		for (Position surrounding : surroundingFields) {
			if (baseMap.containsKey(surrounding)) {
				EField surroundingField = fullMap.getPositionField(surrounding);
				if (surroundingField.equals(EField.GRASS)) {
					surroundingGrassFields.add(surrounding);
				}
			}
		}
		logger.info("Surrounding grass fields list is empty : {}", surroundingFields.isEmpty());
		return surroundingGrassFields;
	}

	private List<Position> getSurroundingCoordinates(Position currentPosition) {
		final int currentXCoordinate = currentPosition.getXCoordinate();
		final int currentYCoordinate = currentPosition.getYCoordinate();

		Position north = new Position(currentXCoordinate, currentYCoordinate - 1);
		Position northEast = new Position(currentXCoordinate - 1, currentYCoordinate - 1);
		Position east = new Position(currentXCoordinate - 1, currentYCoordinate);
		Position southEast = new Position(currentXCoordinate - 1, currentYCoordinate + 1);
		Position south = new Position(currentXCoordinate, currentYCoordinate + 1);
		Position southWest = new Position(currentXCoordinate + 1, currentYCoordinate + 1);
		Position west = new Position(currentXCoordinate + 1, currentYCoordinate);
		Position northWest = new Position(currentXCoordinate + 1, currentYCoordinate - 1);

		List<Position> surroundingFields = Arrays.asList(north, northEast, east, southEast, south, southWest, west,
				northWest);
		return surroundingFields;

	}
}
