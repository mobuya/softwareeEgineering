package rules;

import game.GameRegistry;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import server.exceptions.InvalidGameIDException;

public class ValidGameIDRule implements IRule {
	private final GameRegistry registry;

	public ValidGameIDRule(GameRegistry currentGames) {
		this.registry = currentGames;
	}

	@Override
	public void validateRegister(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {
		checkIfGameIdIsUsed(gameID);
	}

	@Override
	public void validateMap(UniqueGameIdentifier gameID, PlayerHalfMap halfMap) {
		checkIfGameIdIsUsed(gameID);
	}

	@Override
	public void validateGameState(UniqueGameIdentifier gameID, UniquePlayerIdentifier playerID) {
		checkIfGameIdIsUsed(gameID);
	}

	private void checkIfGameIdIsUsed(UniqueGameIdentifier gameID) {
		if (!registry.checkIfGameWithIdExists(gameID.getUniqueGameID())) {
			throw new InvalidGameIDException(
					"This GameID is not valid. It does not exist in the Game Registry anymore. The GameID requested was: "
							+ gameID.getUniqueGameID());
		}
	}
}
