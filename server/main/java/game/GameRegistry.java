package game;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.data.EPlayerTurn;
import game.data.IdGenerator;
import game.data.PlayerInformation;
import game.data.map.ServerHalfMap;

public class GameRegistry {

	private final Logger logger = LoggerFactory.getLogger(GameRegistry.class);
	private static Map<String, Game> currentGames = new LinkedHashMap<>();

	public Game createNewGame() {
		IdGenerator generator = new IdGenerator();
		String randomGameID = generator.generateRandomGameID();

		boolean gameWithIdExists = checkIfGameWithIdExists(randomGameID);

		while (gameWithIdExists) {
			logger.debug(
					"An GameID was generated which already exists in the Current Games list. GameID was regenerated.");
			randomGameID = generator.generateRandomGameID();
			gameWithIdExists = checkIfGameWithIdExists(randomGameID);
		}

		Game newGame = new Game(randomGameID);
		addNewGame(newGame);

		return newGame;
	}

	public String registerPlayerForGame(String gameID, PlayerInformation playerInfo) {
		IdGenerator generator = new IdGenerator();
		String newPlayerID = generator.generateRandomPlayerID();
		currentGames.get(gameID).registerPlayer(newPlayerID, playerInfo);

		return newPlayerID;
	}

	private void addNewGame(Game newGame) {
		final int MAXIMUM_CONCURRENT_GAMES = 99;
		if (currentGames.size() == MAXIMUM_CONCURRENT_GAMES) {
			Game oldestGame = currentGames.values().stream().findFirst().get();
			removeGame(oldestGame.getGameId());
			logger.warn(
					"New Game was created while there were 99 games running; first Game was removed with GameID {}.",
					oldestGame.getGameId());
		}

		currentGames.put(newGame.getGameId(), newGame);
		logger.info("Added new Game, currently active games: {}", currentGames.size());
	}

	public Game getGame(String gameID) {
		return currentGames.get(gameID);
	}

	private void removeGame(String gameID) {
		if (checkIfGameWithIdExists(gameID)) {
			currentGames.remove(gameID);
		}
	}

	public void handlePlayerRuleViolation(String gameID, String playerID) {
		if (checkIfGameWithIdExists(gameID)) {
			Game currentGame = this.getGame(gameID);
			Map<String, PlayerInformation> players = currentGame.getPlayers();

			if (players.containsKey(playerID)) {
				PlayerInformation player = players.get(playerID);
				// the player that violated a rule lost
				player.setPlayerTurnState(EPlayerTurn.LOST);

				for (var eachPlayer : players.entrySet()) {
					if (!eachPlayer.getKey().equals(playerID)) {
						// the opposite playes is now the winner
						eachPlayer.getValue().setPlayerTurnState(EPlayerTurn.WON);
					}
				}
			}
		}
	}

	public void preparePlayerToSendMap(String gameID) {
		Game currentGame = this.getGame(gameID);
		currentGame.chooseRandomFirstPlayer();

		currentGame.updateGamestateID();
	}

	public void processRecievedMapHalf(String gameID, ServerHalfMap halfMap, String playerID) {
		Game currentGame = getGame(gameID);
		currentGame.recieveMapHalf(playerID, halfMap);
		currentGame.switchPlayersTurn(playerID);

		currentGame.updateGamestateID();
	}

	public boolean checkIfGameWithIdExists(String newGameID) {
		return currentGames.containsKey(newGameID);
	}

}
