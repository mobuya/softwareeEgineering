package move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import exceptions.InvalidMapCharacterPosition;
import map.EField;
import map.FullMap;
import map.Position;

class PathFinderTest {

	private static FullMap fullMap;

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
	void startIsThreeGrassAboveGoal_calculateshortestPath_LenghtShouldBe4() {
		PathFinder pathFinder = new PathFinder(fullMap);
		Position startPosition = new Position(0, 0);
		Position goalPosition = new Position(0, 3);

		List<Position> pathToGoal = pathFinder.getPathToGoal(startPosition, goalPosition);

		Assertions.assertEquals(4, pathToGoal.size());
	}

	@Test
	void startIsOppositeCorner_calculateShortestPath_LenghtShouldBe14() {
		PathFinder pathFinder = new PathFinder(fullMap);
		Position startPosition = new Position(0, 0);
		Position goalPosition = new Position(9, 4);

		List<Position> pathToGoal = pathFinder.getPathToGoal(startPosition, goalPosition);
		Assertions.assertEquals(14, pathToGoal.size());
	}

	@Test
	void startIsstartIsThreeGrassAboveGoal_calculateShortestDirections_ShouldReturnThreeDowns() {
		List<EDirection> expectedPath = new ArrayList<>(
				Arrays.asList(EDirection.DOWN, EDirection.DOWN, EDirection.DOWN));
		PathFinder pathFinder = new PathFinder(fullMap);
		Position startPosition = new Position(0, 0);
		Position goalPosition = new Position(0, 3);

		List<Position> pathToGoal = pathFinder.getPathToGoal(startPosition, goalPosition);
		List<EDirection> directionToGoal = pathFinder.convertToMoves(pathToGoal);

		Assertions.assertEquals(expectedPath, directionToGoal);
	}

	@Test
	void startAndGoalOppositeCorners_calculateShortestdirection_ShouldMatchExpectedList() {
		List<EDirection> expectedPath = new ArrayList<>(Arrays.asList(EDirection.DOWN, EDirection.DOWN, EDirection.DOWN,
				EDirection.DOWN, EDirection.RIGHT, EDirection.RIGHT, EDirection.RIGHT, EDirection.RIGHT,
				EDirection.RIGHT, EDirection.RIGHT, EDirection.RIGHT, EDirection.RIGHT, EDirection.RIGHT));
		PathFinder pathFinder = new PathFinder(fullMap);
		Position startPosition = new Position(0, 0);
		Position goalPosition = new Position(9, 4);

		List<Position> pathToGoal = pathFinder.getPathToGoal(startPosition, goalPosition);
		List<EDirection> directionToGoal = pathFinder.convertToMoves(pathToGoal);

		Assertions.assertEquals(expectedPath, directionToGoal);
	}

	@Test
	void goalIsUpperLeftField_calculateShortestPath_PathShouldBeSize3() {
		PathFinder pathFinder = new PathFinder(fullMap);
		Position startPosition = new Position(1, 1);
		Position goalPosition = new Position(0, 0);

		List<Position> pathToGoal = pathFinder.getPathToGoal(startPosition, goalPosition);

		Assertions.assertEquals(3, pathToGoal.size());
	}
}
