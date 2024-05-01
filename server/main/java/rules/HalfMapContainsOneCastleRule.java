package rules;

import java.util.Collection;

import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidCastleCountPresentException;

public class HalfMapContainsOneCastleRule implements IRule {

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		String playerID = halfMap.getUniquePlayerID();

		checkIfHalfMapContainsOneCastle(halfMap, playerID);
		/*
		 * this test is done with clients data type; since my ServerHalfMap does not
		 * support having multiple castles
		 */
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkIfHalfMapContainsOneCastle(PlayerHalfMap halfMap, String playerID) {
		Collection<PlayerHalfMapNode> mapNodes = halfMap.getMapNodes();
		final int REQUIRED_CASTLE_COUNT = 1;
		int foundCastles = 0;

		for (var eachNode : mapNodes) {
			if (eachNode.isFortPresent()) {
				foundCastles++;
			}
		}

		if (foundCastles != REQUIRED_CASTLE_COUNT) {
			throw new InvalidCastleCountPresentException("Castle count was incorrect: expected 1 but found "
					+ foundCastles + ", player who violated the rule is: " + playerID);
		}

	}

}
