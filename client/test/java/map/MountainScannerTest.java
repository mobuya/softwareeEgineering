package map;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import exceptions.InvalidMapCharacterPosition;

class MountainScannerTest {

	private static FullMap fullMap;
	private MountainScanner scanner = new MountainScanner();

	@BeforeAll
	public static void setUpBeforeClass() throws InvalidMapCharacterPosition {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 10;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.MOUNTAIN;
				if (xCounter % 2 == 0) {
					field = EField.GRASS;
				}
				map.put(newPosition, field);
			}
		}
		fullMap = new FullMap(map);
	}

	@Test
	void mountainSurroundedBySixGrass_getSurroundingGrassFields_ReturnListSize6() {
		Position mountainPosition = new Position(3, 3);

		List<Position> surroundingGrassFields = scanner.getSurroundingGrassFields(fullMap, mountainPosition);

		Assertions.assertEquals(6, surroundingGrassFields.size());
	}

}
