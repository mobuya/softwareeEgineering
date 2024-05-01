package rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import game.data.DataConverter;
import game.data.map.Position;
import game.data.map.ServerHalfMap;
import game.data.map.constants.EField;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.MapContainsUnreachableFieldException;

public class HalfMapContainsNoUnreachableFieldsRule implements IRule {

	private DataConverter converter = new DataConverter();

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		String playerID = halfMap.getUniquePlayerID();
		checkForUnreachableFields(sHalfMap, playerID);

	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkForUnreachableFields(ServerHalfMap halfMap, String playerID) {
		Map<Position, EField> baseMap = halfMap.getBaseMap();
		List<Position> reachableFields = new ArrayList<>();

		Position startPosition = baseMap.entrySet().stream().filter(entry -> entry.getValue().equals(EField.GRASS))
				.map(Map.Entry::getKey).findFirst().orElse(null);

		floodFill(startPosition, baseMap, reachableFields);

		long availableFields = baseMap.values().stream().filter(field -> !field.equals(EField.WATER)).count();
		int numberOfReachableFields = reachableFields.size();

		if (numberOfReachableFields < availableFields) {
			int numberOfUnreachableFields = (int) (availableFields - numberOfReachableFields);
			throw new MapContainsUnreachableFieldException("Map contains " + numberOfUnreachableFields
					+ "unreachable fields! The player who violated this rule is: " + playerID);
		}

	}

	private void floodFill(Position startPosition, Map<Position, EField> baseMap, List<Position> reachableFields) {
		if (!baseMap.containsKey(startPosition) || reachableFields.contains(startPosition)) {
			return;
		}
		if (baseMap.get(startPosition).equals(EField.WATER)) {
			return;
		}

		reachableFields.add(startPosition);

		List<Position> neighbourPositions = getNeighbours(startPosition, baseMap);

		for (Position eachPosition : neighbourPositions) {
			floodFill(eachPosition, baseMap, reachableFields);
		}

	}

	private List<Position> getNeighbours(Position position, Map<Position, EField> baseMap) {
		List<Position> neighbours = new ArrayList<>();
		final int currentX = position.getXCoordinate();
		final int currentY = position.getYCoordinate();

		Position above = new Position(currentX, currentY - 1);
		Position below = new Position(currentX, currentY + 1);
		Position left = new Position(currentX - 1, currentY);
		Position right = new Position(currentX + 1, currentY);

		neighbours.addAll(List.of(above, below, left, right));
		return neighbours;
	}

}
