package rules;

import java.util.Map;

import game.data.DataConverter;
import game.data.map.Position;
import game.data.map.ServerHalfMap;
import game.data.map.constants.EField;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidWaterFieldBorderCountException;

public class WaterFieldsOnBorderCountRule implements IRule {

	private final DataConverter converter = new DataConverter();

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		String playerId = halfMap.getUniquePlayerID();

		checkWaterFieldCountOnBroder(sHalfMap, playerId);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkWaterFieldCountOnBroder(ServerHalfMap halfMap, String playerID) {
		final int VERTICAL_BORDER_LIMIT = 2;
		final int HORIZONTAL_BORDER_LIMIT = 4;

		final int Y_COORDINATE_UPPER_BORDER = 0;
		final int Y_COORDINATE_LOWER_BORDER = 4;
		final int X_COORDINATE_LEFT_BORDER = 0;
		final int X_COORDINATE_RIGHT_BORDER = 9;

		Map<Position, EField> baseMap = halfMap.getBaseMap();

		long upperHorizontalBorderCount = baseMap.entrySet().stream()
				.filter(eachMapTile -> eachMapTile.getKey().getYCoordinate() == Y_COORDINATE_UPPER_BORDER
						&& eachMapTile.getValue().equals(EField.WATER))
				.count();
		long lowerHorizontalBorderCount = baseMap.entrySet().stream()
				.filter(eachMapTile -> eachMapTile.getKey().getYCoordinate() == Y_COORDINATE_LOWER_BORDER
						&& eachMapTile.getValue().equals(EField.WATER))
				.count();
		long leftVerticalBorderCount = baseMap.entrySet().stream()
				.filter(eachMapTile -> eachMapTile.getKey().getXCoordinate() == X_COORDINATE_LEFT_BORDER
						&& eachMapTile.getValue().equals(EField.WATER))
				.count();
		long rightVerticalBorderCount = baseMap.entrySet().stream()
				.filter(eachMapTile -> eachMapTile.getKey().getXCoordinate() == X_COORDINATE_RIGHT_BORDER
						&& eachMapTile.getValue().equals(EField.WATER))
				.count();

		if (upperHorizontalBorderCount > HORIZONTAL_BORDER_LIMIT) {
			throw new InvalidWaterFieldBorderCountException(HORIZONTAL_BORDER_LIMIT, upperHorizontalBorderCount,
					playerID);
		}

		if (lowerHorizontalBorderCount > HORIZONTAL_BORDER_LIMIT) {
			throw new InvalidWaterFieldBorderCountException(HORIZONTAL_BORDER_LIMIT, lowerHorizontalBorderCount,
					playerID);
		}

		if (leftVerticalBorderCount > VERTICAL_BORDER_LIMIT) {
			throw new InvalidWaterFieldBorderCountException(VERTICAL_BORDER_LIMIT, leftVerticalBorderCount, playerID);
		}

		if (rightVerticalBorderCount > VERTICAL_BORDER_LIMIT) {
			throw new InvalidWaterFieldBorderCountException(VERTICAL_BORDER_LIMIT, rightVerticalBorderCount, playerID);
		}

	}

}
