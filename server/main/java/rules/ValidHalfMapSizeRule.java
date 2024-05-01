package rules;

import game.data.DataConverter;
import game.data.map.ServerHalfMap;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidMapHalfSizeException;

public class ValidHalfMapSizeRule implements IRule {

	private final DataConverter converter = new DataConverter();

	public ValidHalfMapSizeRule() {
	}

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		ServerHalfMap sHalfMap = converter.convertClientMapToServerMap(halfMap);
		String playerID = halfMap.getUniquePlayerID();
		checkIfMapHalfSizeIs50(sHalfMap, playerID);

	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkIfMapHalfSizeIs50(ServerHalfMap halfMap, String playerID) {
		final int REQUIERED_HALFMAP_SIZE = 50;
		int halfMapSize = halfMap.getMapSize();

		if (halfMapSize != REQUIERED_HALFMAP_SIZE) {
			throw new InvalidMapHalfSizeException(
					"The sent MapHalf was not of fize 50, the player who violated this rule is: " + playerID);
		}
	}

}
