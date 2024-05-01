package map;

import java.util.LinkedHashMap;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.provider.MethodSource;

import exceptions.InvalidMapCharacterPosition;

class FullMapTest {

	private static FullMap fullMap;

	@BeforeAll
	public static void setUpBeforeClass() throws InvalidMapCharacterPosition {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 10;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.values()[new Random().nextInt(EField.values().length)];
				map.put(newPosition, field);
			}
		}
		fullMap = new FullMap(map);
		Position randomMyPlayerPosition = new Position(2, 2);
		fullMap.placePlayerOnMap(EMapCharacter.MY_PLAYER, randomMyPlayerPosition);
		fullMap.placeCastleOnMap(EMapCharacter.MY_PLAYER, randomMyPlayerPosition);
		Position randomEnemyPlayerPosition = new Position(4, 4);
		fullMap.placePlayerOnMap(EMapCharacter.ENEMY, randomEnemyPlayerPosition);
		fullMap.placeCastleOnMap(EMapCharacter.ENEMY, randomEnemyPlayerPosition);

		Position randomTreasure = new Position(5, 5);
		fullMap.placeTreasure(randomTreasure);

	}

	@Test
	void verticalMap_CheckIfHorizontal_ReturnFalse() {

		boolean verticalMap = fullMap.checkIfMapHotizontal();

		Assertions.assertEquals(false, verticalMap);
	}

	@Test
	@MethodSource("createVerticalGrassMap")
	void horizontalMap_checkIfHorizontal_ReturnTrue() {
		boolean verticalMap = createVerticalGrassMap().checkIfMapHotizontal();

		Assertions.assertEquals(true, verticalMap);
	}

	@Test
	@MethodSource("createVerticalGrassMap")
	void grassMap_getAnyPosition_returnGrassField() {
		FullMap fullMap = createVerticalGrassMap();
		Position randomPosition = new Position(0, 0);

		EField field = fullMap.getPositionField(randomPosition);

		Assertions.assertEquals(EField.GRASS, field);
	}

	@Test
	void noPlayerOnMap_PlacePlayerOutsideMap_GetInvalidMapCharacterPosition() {
		Position invalidPosition = new Position(20, 100);
		Executable placeCharacterOutsideMap = () -> {
			fullMap.placePlayerOnMap(EMapCharacter.MY_PLAYER, invalidPosition);
		};

		Assertions.assertThrows(InvalidMapCharacterPosition.class, placeCharacterOutsideMap);
	}

	@Test
	void fullMap_getTheBaseMap_CheckIfSizeIs100() {
		int fullMapSize = fullMap.getBaseMap().size();

		Assertions.assertEquals(100, fullMapSize);
	}

	@Test
	void myPlayerPositionX2Y2_getMyPlayerPosition_ReturnX2Y2() {
		Position myPlayerPosition = fullMap.getPlayerPosition(EMapCharacter.MY_PLAYER);
		Position expectedPosition = new Position(2, 2);

		Assertions.assertEquals(expectedPosition, myPlayerPosition);
	}

	@Test
	void enemyCastlepositionX4Y4_getEnemyCastlePosition_ReturnX4Y4() {
		Position enemyCastlePosition = fullMap.getCastlePosition(EMapCharacter.ENEMY);
		Position expectedPosition = new Position(4, 4);

		Assertions.assertEquals(expectedPosition, enemyCastlePosition);
	}

	@Test
	void trasurePositionX5Y5_getTreasurePosition_Returnx5Y5() {
		Position treasurePosition = fullMap.getTreasurePosition();
		Position expectedPosition = new Position(5, 5);

		Assertions.assertEquals(expectedPosition, treasurePosition);
	}

	@Test
	@MethodSource("createVerticalGrassMap")
	void noCastleOnMap_placeCastleOutsideMap_GetInvalidMapCharacterPosition() {
		FullMap map = createVerticalGrassMap();
		Position invalidPosition = new Position(20, 20);

		Executable placeCastleOutsideMap = () -> {
			map.placeCastleOnMap(EMapCharacter.ENEMY, invalidPosition);
		};

		Assertions.assertThrows(InvalidMapCharacterPosition.class, placeCastleOutsideMap);
	}

	public static FullMap createVerticalGrassMap() {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();
		final int X_LIMIT = 20;
		final int Y_LIMIT = 5;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.GRASS;
				map.put(newPosition, field);
			}
		}
		return new FullMap(map);
	}

}
