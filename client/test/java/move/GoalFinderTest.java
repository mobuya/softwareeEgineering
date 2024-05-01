package move;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.provider.MethodSource;

import map.EField;
import map.FullMap;
import map.Position;

class GoalFinderTest {
	private static Position onlyGrassPosition;
	private static Position onlyMountainPosition;

	@BeforeAll
	public static void createRandomGrassField() {
		Random random = new Random();
		int randomXCoordinate = random.nextInt(0, 9);
		int randomYCoordinate = random.nextInt(0, 4);

		onlyGrassPosition = new Position(randomXCoordinate, randomYCoordinate);
	}

	@BeforeAll
	public static void createRandomMountainField() {
		Random random = new Random();
		int randomXCoordinate = random.nextInt(9, 19);
		int randomYCoordinate = random.nextInt(0, 4);

		onlyMountainPosition = new Position(randomXCoordinate, randomYCoordinate);
	}

	@RepeatedTest(value = 10)
	@MethodSource("createMapWithOneGrassField")
	void mapHasOnlyOneGrassField_searchForRandomGrassgoal_GetTheonlyGrassField() {
		FullMap map = createMapWithOneGrassField();
		Position startPosition = new Position(0, 0);
		GoalFinder goalFinder = new GoalFinder(startPosition);

		Position grassGoal = goalFinder.findRandomGoal(map, new ArrayList<Position>());

		Assertions.assertEquals(onlyGrassPosition, grassGoal);
	}

	@RepeatedTest(value = 10)
	@MethodSource("createVericalMapWithOneMountainField")
	void verticalMapWithEnemyMountain_searchForEnemyMountain_GetTheMountainPosition() {
		FullMap map = createVericalMapWithOneMountainField();
		Position startPosition = new Position(8, 8);
		GoalFinder goalFinder = new GoalFinder(startPosition);
		Position enemyMountain = new Position(0, 0);

		Position mountainGoal = goalFinder.getRandomEnemySideMountainField(map);
		Assertions.assertEquals(enemyMountain, mountainGoal);
	}

	@RepeatedTest(value = 10)
	@MethodSource("createMapWithOneMountainField")
	void horizontalMapOnlyMountainOnEnemySide_searchForEnemyMountain_GetTheEnemyMountainGoal() {
		FullMap map = createMapWithOneMountainField();
		Position startPosition = new Position(0, 0);
		GoalFinder goalFinder = new GoalFinder(startPosition);

		Position mountainGoal = goalFinder.getRandomEnemySideMountainField(map);

		Assertions.assertEquals(onlyMountainPosition, mountainGoal);
	}

	private static FullMap createMapWithOneGrassField() {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();

		final int X_LIMIT = 20;
		final int Y_LIMIT = 5;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.MOUNTAIN;
				map.put(newPosition, field);
			}
		}
		map.put(onlyGrassPosition, EField.GRASS);
		return new FullMap(map);
	}

	private static FullMap createMapWithOneMountainField() {
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
		map.put(onlyMountainPosition, EField.MOUNTAIN);
		return new FullMap(map);
	}

	private static FullMap createVericalMapWithOneMountainField() {
		LinkedHashMap<Position, EField> map = new LinkedHashMap<>();
		Position enemyMountainSide = new Position(0, 0);

		final int X_LIMIT = 10;
		final int Y_LIMIT = 10;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.GRASS;
				map.put(newPosition, field);
			}
		}
		map.put(enemyMountainSide, EField.MOUNTAIN);
		return new FullMap(map);
	}
}
