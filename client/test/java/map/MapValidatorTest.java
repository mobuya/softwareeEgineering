package map;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class MapValidatorTest {
	MapGenerator generator = new MapGenerator();
	MapValidator validator = new MapValidator();

	@RepeatedTest(value = 10)
	void createValidMap_validateMap_retunBooleanTrue() {
		LinkedHashMap<Position, EField> validMap = generator.createValidMap();

		boolean mapValid = validator.invalidateHalfMap(validMap);

		Assertions.assertEquals(true, mapValid);
	}

	@Test
	void createHalfMapWithNoCastle_validateCastle_returnBoolenFalse() {
		LinkedHashMap<Position, EField> halfMap = generator.createValidMap();
		Position treasurePosition = null;
		for (Map.Entry<Position, EField> mapTile : halfMap.entrySet()) {
			if (mapTile.getKey().hasMyCastle()) {
				treasurePosition = mapTile.getKey();
				break;
			}
		}
		halfMap.remove(treasurePosition);

		boolean mapHasCastle = validator.checkIfCastlePresent(halfMap);

		Assertions.assertEquals(false, mapHasCastle);
	}

}
