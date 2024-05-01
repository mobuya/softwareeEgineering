package rules;

import game.Game;
import game.GameRegistry;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidPlayerIDForRunningGameException;

public class ValidPlayerIDRule implements IRule {

	private final GameRegistry registry;

	public ValidPlayerIDRule(GameRegistry currentGames) {
		this.registry = currentGames;
	}

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		UniquePlayerIdentifier playerID = new UniquePlayerIdentifier(halfMap.getUniquePlayerID());
		checkIfPlayerIDIsValid(gameID, playerID);

	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {
		checkIfPlayerIDIsValid(gameID, playerID);
	}

	private void checkIfPlayerIDIsValid(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {
		Game currentGame = registry.getGame(gameID.getUniqueGameID());
		if (!currentGame.getPlayers().containsKey(playerID.getUniquePlayerID())) {
			throw new InvalidPlayerIDForRunningGameException(
					"Client sent a MapHalf with an invalid PlayerID. The PLayerID was: " + playerID);
		}
	}
}
