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
import server.exceptions.InvalidMapFieldCountException;

public class ValidMapFieldTypeCountRule implements IRule {

	private final DataConverter converter = new DataConverter();

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {
	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		String playerID = halfMap.getUniquePlayerID();
		checkIfMapFieldTypeCountIsValid(sHalfMap, playerID);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {
	}

	private void checkIfMapFieldTypeCountIsValid(ServerHalfMap halfMap, String playerID) {
		final int MINIMUM_GRASS_FIELDS = 24;
		final int MINIMUM_WATER_FIELDS = 7;
		final int MINIMUM_MOUNTAIN_FIELS = 5;
		Map<Position, EField> baseMap = halfMap.getBaseMap();

		long grassCount = baseMap.values().stream().filter(field -> field.equals(EField.GRASS)).count();
		long waterCount = baseMap.values().stream().filter(field -> field.equals(EField.WATER)).count();
		long mountainCount = baseMap.values().stream().filter(field -> field.equals(EField.MOUNTAIN)).count();

		if (grassCount < MINIMUM_GRASS_FIELDS) {
			throw new InvalidMapFieldCountException(
					"There was not enough grass fields in Map half, found: " + grassCount + " but expected "
							+ MINIMUM_GRASS_FIELDS + ". The player who violated this rule is: " + playerID);
		}

		if (waterCount < MINIMUM_WATER_FIELDS) {
			throw new InvalidMapFieldCountException(
					"There was not enough water fields in Map half, found: " + waterCount + " but expected "
							+ MINIMUM_WATER_FIELDS + ". The player who violated this rule is: " + playerID);
		}

		if (mountainCount < MINIMUM_MOUNTAIN_FIELS) {
			throw new InvalidMapFieldCountException(
					"There was not enough mountain fields in Map half, found: " + mountainCount + " but expected "
							+ MINIMUM_MOUNTAIN_FIELS + ". The player who violated this rule is: " + playerID);
		}
	}

}
