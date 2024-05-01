package rules;

import game.Game;
import game.GameRegistry;
import game.data.PlayerInformation;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.MapAlreadySubmittedException;

public class HalfMapCanBeSentOnceRule implements IRule {

	private final GameRegistry registry;

	public HalfMapCanBeSentOnceRule(GameRegistry currentGames) {
		this.registry = currentGames;
	}

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {

	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		String playerID = halfMap.getUniquePlayerID();
		checkIfMapWasAlredySent(gameID, playerID);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {

	}

	private void checkIfMapWasAlredySent(UniqueGameIdentifier gameID, String playerID) {
		Game currentGame = this.registry.getGame(gameID.getUniqueGameID());
		PlayerInformation player = currentGame.getPlayerInformation(playerID);

		if (player.isMapHalfSent()) {
			throw new MapAlreadySubmittedException(
					"Map can only be sent once during the game, the player who violated this rule: " + playerID);
		}

	}

}
