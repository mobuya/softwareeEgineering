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
import server.exceptions.InvalidCastleFieldException;

public class CastlePositionIsGrassRule implements IRule {

	private DataConverter converter = new DataConverter();

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		String playerID = halfMap.getUniquePlayerID();
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		checkIfstartPositionIsGrass(sHalfMap, playerID);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkIfstartPositionIsGrass(ServerHalfMap halfMap, String playerID) {
		Position castlePosition = halfMap.getCastlePosition();
		Map<Position, EField> baseMap = halfMap.getBaseMap();

		if (!baseMap.get(castlePosition).equals(EField.GRASS)) {
			throw new InvalidCastleFieldException(
					"HalfMap castle position was not on Grass, the player responsible is " + playerID);
		}

	}

}
