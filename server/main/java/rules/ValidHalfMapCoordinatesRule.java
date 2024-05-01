package rules;

import java.util.Collection;
import java.util.Map;

import game.data.DataConverter;
import game.data.map.Position;
import game.data.map.ServerHalfMap;
import game.data.map.constants.EField;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidHalfMapCoordinateException;

public class ValidHalfMapCoordinatesRule implements IRule {

	private final DataConverter converter = new DataConverter();

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		String playerID = halfMap.getUniquePlayerID();
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		checkHalfMapCoordinates(sHalfMap, playerID);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkHalfMapCoordinates(ServerHalfMap halfMap, String playerID) {
		Map<Position, EField> baseMap = halfMap.getBaseMap();
		Collection<Position> mapCoordinates = baseMap.keySet();
		final int X_COORDINATE_LIMIT = 9;
		final int Y_COORDINATE_LIMIT = 4;

		for (var eachCoordinate : mapCoordinates) {
			if (eachCoordinate.getXCoordinate() > X_COORDINATE_LIMIT) {
				throw new InvalidHalfMapCoordinateException(
						"The HalfMap contained an invalid X Coordinate, the map should be 10x5(x 10, y 5), found X with value : "
								+ eachCoordinate.getXCoordinate() + ", player responsible is: " + playerID);
			}
			if (eachCoordinate.getYCoordinate() > Y_COORDINATE_LIMIT) {
				throw new InvalidHalfMapCoordinateException(
						"The HalfMap contained an invalid X Coordinate, the map should be 10x5(x 10, y 5), found Y with value : "
								+ eachCoordinate.getYCoordinate() + ", player responsible is: " + playerID);
			}
		}
	}

}
