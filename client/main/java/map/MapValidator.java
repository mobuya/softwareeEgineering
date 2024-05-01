package map;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapValidator {

	public boolean invalidateHalfMap(LinkedHashMap<Position, EField> halfMap) {
		boolean isMapValid = this.checkIfMapSizeValid(halfMap) && this.checkWaterOnBoardersValid(halfMap)
				&& this.isGrassCountValid(halfMap) && this.isMountainCountValid(halfMap)
				&& this.isWaterCountValid(halfMap);
		return isMapValid;
	}

	private boolean checkIfMapSizeValid(LinkedHashMap<Position, EField> halfMap) {
		return halfMap.size() == 50;
	}

	public boolean checkIfCastlePresent(LinkedHashMap<Position, EField> halfMap) {
		boolean mapHasCastle = false;
		for (Map.Entry<Position, EField> mapTile : halfMap.entrySet()) {
			if (mapTile.getKey().hasMyCastle()) {
				mapHasCastle = true;
				break;
			}
		}
		return mapHasCastle;
	}

	private boolean checkWaterOnBoardersValid(LinkedHashMap<Position, EField> halfMap) {
		boolean waterOnBoarders = false;

		int waterVerticalCounter = 0;
		int waterHorizontal = 0;
		final int HORIZONTAL_WATER_LIMIT = 4;
		final int VERTICAL_WATER_LIMIT = 2;
		final int LEFT_MAP_SIDE = 0;
		final int RIGHT_MAP_SIDE = 9;
		final int UPPER_MAP_SIDE = 0;
		final int DOWN_MAP_SIDE = 4;

		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			if (field.getKey().getXCoordinate() == RIGHT_MAP_SIDE || field.getKey().getXCoordinate() == LEFT_MAP_SIDE) {
				if (field.getValue() == EField.WATER) {
					waterVerticalCounter++;
				}
			}
			if (field.getKey().getYCoordinate() == UPPER_MAP_SIDE || field.getKey().getYCoordinate() == DOWN_MAP_SIDE) {
				if (field.getValue() == EField.WATER) {
					waterHorizontal++;
				}
			}
		}
		if (waterVerticalCounter <= VERTICAL_WATER_LIMIT && waterHorizontal <= HORIZONTAL_WATER_LIMIT) {
			waterOnBoarders = true;
		}
		return waterOnBoarders;

	}

	private boolean isWaterCountValid(LinkedHashMap<Position, EField> halfMap) {
		boolean mapIsValid = false;
		final int WATER_MINIMUM = 7;
		int waterCounter = 0;
		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			if (field.getValue() == EField.WATER) {
				waterCounter++;
			}
		}

		if (waterCounter >= WATER_MINIMUM) {
			mapIsValid = true;
		}
		return mapIsValid;
	}

	private boolean isGrassCountValid(LinkedHashMap<Position, EField> halfMap) {
		boolean mapIsValid = false;
		final int GRASS_MINIMUM = 24;
		int grassCounter = 0;

		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			if (field.getValue() == EField.GRASS) {
				grassCounter++;
			}
		}

		if (grassCounter > GRASS_MINIMUM) {
			mapIsValid = true;
		}
		return mapIsValid;

	}

	private boolean isMountainCountValid(LinkedHashMap<Position, EField> halfMap) {
		boolean mapIsValid = false;
		final int MOUNTAIN_MINIMUM = 5;
		int mountainCounter = 0;

		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {
			if (field.getValue() == EField.MOUNTAIN) {
				mountainCounter++;
			}
		}

		if (mountainCounter >= MOUNTAIN_MINIMUM) {
			mapIsValid = true;
		}
		return mapIsValid;

	}

}
