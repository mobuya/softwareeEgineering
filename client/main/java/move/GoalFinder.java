package move;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import map.EField;
import map.FullMap;
import map.Position;

public class GoalFinder {
	private final Logger logger = LoggerFactory.getLogger(GoalFinder.class);
	private Position clientStartPosition;

	public GoalFinder(Position plarStartPosition) {
		this.clientStartPosition = plarStartPosition;
	}

	public Position findRandomGoal(FullMap map, List<Position> visitedFields) {
		int randomXCoordinate = getRandomXCoordinate(map);
		int randomYCoordinate = getRandomYCoordinate(map);

		Position randomPosition = new Position(randomXCoordinate, randomYCoordinate);
		EField randomPositionField = map.getPositionField(randomPosition);
		boolean validGoal = checkIfGoalValid(randomPosition, randomPositionField, visitedFields);
		while (!validGoal) {
			randomXCoordinate = getRandomXCoordinate(map);
			randomYCoordinate = getRandomYCoordinate(map);
			randomPosition = new Position(randomXCoordinate, randomYCoordinate);
			randomPositionField = map.getPositionField(randomPosition);
			validGoal = checkIfGoalValid(randomPosition, randomPositionField, visitedFields);
		}
		return randomPosition;
	}

	private boolean checkIfEnemySideMountain(Position goal, EField goalField) {

		final int X_COORDINATE_LIMIT = 9;
		final int Y_COORDINATE_LIMIT = 4;

		if (!goalField.equals(EField.MOUNTAIN)) {
			return false;
		} else if (goal.equals(clientStartPosition)) {
			return false;
		} else if (clientStartPosition.getXCoordinate() <= X_COORDINATE_LIMIT
				&& goal.getXCoordinate() > X_COORDINATE_LIMIT) {
			return true;
		} else if (clientStartPosition.getXCoordinate() > X_COORDINATE_LIMIT
				&& goal.getXCoordinate() <= X_COORDINATE_LIMIT) {
			return true;
		} else if (clientStartPosition.getYCoordinate() > Y_COORDINATE_LIMIT
				&& goal.getYCoordinate() <= Y_COORDINATE_LIMIT) {
			return true;
		} else if (clientStartPosition.getYCoordinate() <= Y_COORDINATE_LIMIT
				&& goal.getYCoordinate() > Y_COORDINATE_LIMIT) {
			return true;
		} else {
			return false;
		}

	}

	public Position getRandomEnemySideMountainField(FullMap map) {
		int randomX = getRandomXCoordinate(map);
		int randomY = getRandomYCoordinate(map);
		Position enemySidePosition = new Position(randomX, randomY);
		EField enemySideField = map.getPositionField(enemySidePosition);
		boolean validposition = checkIfEnemySideMountain(enemySidePosition, enemySideField);
		while (!validposition) {
			int newRandomX = getRandomXCoordinate(map);
			int newRandomY = getRandomYCoordinate(map);
			enemySidePosition = new Position(newRandomX, newRandomY);
			enemySideField = map.getPositionField(enemySidePosition);
			validposition = checkIfEnemySideMountain(enemySidePosition, enemySideField);
		}
		return enemySidePosition;
	}

	private boolean checkIfGoalValid(Position goal, EField goalField, List<Position> visitedFields) {
		final int X_COORDINATE_LIMIT = 9;
		final int Y_COORDINATE_LIMIT = 4;
		if (visitedFields.contains(goal)) {
			return false;
		}
		if (!goalField.equals(EField.GRASS)) {
			return false;
		} else if (goal.equals(clientStartPosition)) {
			return false;
		} else if (clientStartPosition.getXCoordinate() <= X_COORDINATE_LIMIT
				&& goal.getXCoordinate() > X_COORDINATE_LIMIT) {
			return false;
		} else if (clientStartPosition.getXCoordinate() > X_COORDINATE_LIMIT
				&& goal.getXCoordinate() <= X_COORDINATE_LIMIT) {
			return false;
		} else if (clientStartPosition.getYCoordinate() > Y_COORDINATE_LIMIT
				&& goal.getYCoordinate() <= Y_COORDINATE_LIMIT) {
			return false;
		} else if (clientStartPosition.getYCoordinate() <= Y_COORDINATE_LIMIT
				&& goal.getYCoordinate() > Y_COORDINATE_LIMIT) {
			return false;
		} else {
			return true;
		}
	}

	private int getRandomXCoordinate(FullMap map) {
		Random random = new Random();
		int randomXCoordinate;
		if (map.checkIfMapHotizontal()) {
			final int HORIZONTAL_MAP_X_LIMIT = 20;
			randomXCoordinate = random.nextInt(0, HORIZONTAL_MAP_X_LIMIT);
			return randomXCoordinate;
		} else {
			final int VERTICAL_MAP_LIMIT = 10;
			randomXCoordinate = random.nextInt(0, VERTICAL_MAP_LIMIT);
			return randomXCoordinate;
		}
	}

	private int getRandomYCoordinate(FullMap map) {
		Random random = new Random();
		int randomYCoordinate;
		if (map.checkIfMapHotizontal()) {
			final int HORIZONTAL_MAP_Y_LIMIT = 5;
			randomYCoordinate = random.nextInt(0, HORIZONTAL_MAP_Y_LIMIT);
			return randomYCoordinate;
		} else {
			final int VERTICAL_MAP_LIMIT = 10;
			randomYCoordinate = random.nextInt(0, VERTICAL_MAP_LIMIT);
			return randomYCoordinate;
		}
	}

}
