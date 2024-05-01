package move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import map.EField;
import map.FullMap;
import map.Position;

public class PathFinder {
	private final Logger logger = LoggerFactory.getLogger(PathFinder.class);
	private FullMap map;

	public PathFinder(FullMap gameMap) {
		this.map = gameMap;
	}

	public List<Position> getPathToGoal(Position start, Position goal) {
		/*
		 * This method was created with the help of the following tutotial: (contains
		 * pseudocode and a nice animated explanation) "How Dijkstra's Algorithm Works"
		 * by Spanning Tree, Link: https://www.youtube.com/watch?v=EFg3u_E6eHU&t=301s
		 */
		HashMap<Position, Integer> distance = new HashMap<>();
		HashMap<Position, Position> connections = new HashMap<>();
		PriorityQueue<Position> visitedVertex = new PriorityQueue<>(Comparator.comparingInt(distance::get));
		boolean reachedGoal = false;

		distance.put(start, 0);
		visitedVertex.add(start);

		while (!reachedGoal) {
			Position current = visitedVertex.poll();
			if (current.equals(goal)) {
				List<Position> path = new ArrayList<>();
				while (current != null) {
					path.add(current);
					current = connections.get(current);
				}
				reachedGoal = true;
				Collections.reverse(path);
				return path;
			}
			int currentCost = distance.get(current);
			for (Position neighbour : getSurroundingFields(current)) {
				int newMovementCost = currentCost + getMoveCost(current, neighbour);
				if (!distance.containsKey(neighbour) || newMovementCost < distance.get(neighbour)) {
					distance.put(neighbour, newMovementCost);
					connections.put(neighbour, current);
					visitedVertex.add(neighbour);
				}
			}
		}
		logger.warn("No path to wanted goal, empty path");
		return new ArrayList<Position>();
	}

	private List<Position> getSurroundingFields(Position destination) {
		List<Position> surroundingFields = new ArrayList<>();
		LinkedHashMap<Position, EField> baseMap = map.getBaseMap();

		Position aboveField = new Position(destination.getXCoordinate(), destination.getYCoordinate() - 1);
		Position belowField = new Position(destination.getXCoordinate(), destination.getYCoordinate() + 1);
		Position leftField = new Position(destination.getXCoordinate() - 1, destination.getYCoordinate());
		Position rightField = new Position(destination.getXCoordinate() + 1, destination.getYCoordinate());

		List<Position> neighbours = new ArrayList<>(Arrays.asList(aboveField, belowField, leftField, rightField));

		for (Position around : neighbours) {
			if (baseMap.containsKey(around) && !map.getPositionField(around).equals(EField.WATER)) {
				surroundingFields.add(around);
			}
		}
		if (surroundingFields.isEmpty()) {
			logger.info("No valid fields around map tile.");
		}
		return surroundingFields;
	}

	private int getMoveCost(Position current, Position destination) {
		int moveCost = 0;
		final int GRASS_TO_GRASS = 2;
		final int GRASS_TO_MOUNTAIN = 3;
		final int MOUNTAIN_TO_GRASS = 3;
		final int MOUNTAIN_TO_MOUNTAIN = 4;

		if (map.getPositionField(current).equals(EField.GRASS)) {
			if (map.getPositionField(destination).equals(EField.GRASS)) {
				moveCost = GRASS_TO_GRASS;
			} else {
				moveCost = GRASS_TO_MOUNTAIN;
			}
		} else {
			if (map.getPositionField(destination).equals(EField.GRASS)) {
				moveCost = MOUNTAIN_TO_GRASS;
			} else {
				moveCost = MOUNTAIN_TO_MOUNTAIN;
			}
		}
		return moveCost;
	}

	public LinkedList<EDirection> convertToMoves(List<Position> pathToGoal) {
		LinkedList<EDirection> pathDirection = new LinkedList<>();

		Position previous = pathToGoal.get(0);
		for (Position nextPosition : pathToGoal) {
			if (previous.getXCoordinate() == nextPosition.getXCoordinate()) {
				if (nextPosition.getYCoordinate() > previous.getYCoordinate()) {
					pathDirection.add(EDirection.DOWN);
				} else if (nextPosition.getYCoordinate() < previous.getYCoordinate()) {
					pathDirection.add(EDirection.UP);
				}
			} else {
				if (nextPosition.getXCoordinate() > previous.getXCoordinate()) {
					pathDirection.add(EDirection.RIGHT);
				} else if (nextPosition.getXCoordinate() < previous.getXCoordinate()) {
					pathDirection.add(EDirection.LEFT);
				}
			}
			previous = nextPosition;
		}
		return pathDirection;
	}

}
