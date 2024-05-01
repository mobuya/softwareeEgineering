package rules;

import game.Game;
import game.GameRegistry;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.ExceededNumberOfRegistrationsException;

public class PlayersNumberLimitRule implements IRule {

	private final GameRegistry registry;

	public PlayersNumberLimitRule(GameRegistry currentGames) {
		this.registry = currentGames;
	}

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {
		checkNumberOfRegisteredPlayers(gameID);
	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {

	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkNumberOfRegisteredPlayers(UniqueGameIdentifier gameID) {
		Game currentGame = registry.getGame(gameID.getUniqueGameID());

		if (currentGame.limitOfPlayesExceeded()) {
			throw new ExceededNumberOfRegistrationsException(
					"Client tried to register where there was already enough players.");
		}

	}

}
