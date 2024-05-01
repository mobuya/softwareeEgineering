package game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.data.EPlayerTurn;
import game.data.IdGenerator;
import game.data.PlayerInformation;
import game.data.map.ServerFullMap;
import game.data.map.ServerHalfMap;

public class Game {
	private final String gameID;
	private String gameStateID = "";
	private Map<String, PlayerInformation> players = new LinkedHashMap<>();
	private ServerFullMap fullMap = new ServerFullMap();
	private Map<String, ServerHalfMap> halfMaps = new LinkedHashMap<>();
	private final Logger logger = LoggerFactory.getLogger(Game.class);
	private final IdGenerator idGenerator = new IdGenerator();

	public Game(String newGameID) {
		this.gameID = newGameID;
		this.gameStateID = idGenerator.generateRandomGameStateId();
	}

	public String getGameId() {
		return this.gameID;
	}

	public String getGameStateID() {
		return this.gameStateID;
	}

	public void updateGamestateID() {
		this.gameStateID = idGenerator.generateRandomGameStateId();
	}

	public ServerFullMap getFullMapForPlayerID(String playerID) {
		makeFullMap();
		ServerHalfMap oneHalf = halfMaps.get(playerID);

		return fullMap.makeFullMapForPlayer(oneHalf);

	}

	public void registerPlayer(String playerID, PlayerInformation newPlayer) {
		if (!limitOfPlayesExceeded())
			players.put(playerID, newPlayer);
	}

	public boolean limitOfPlayesExceeded() {
		final int NECESSARY_NUMBER_OF_PLAYERS = 2;
		return players.size() > NECESSARY_NUMBER_OF_PLAYERS;
	}

	public Map<String, PlayerInformation> getPlayers() {
		return this.players;
	}

	public PlayerInformation getPlayerInformation(String playerID) {
		if (players.isEmpty() || !players.containsKey(playerID)) {
			logger.info("The game currently doesnt contain information about player with PlayerID: {}", playerID);
		}
		return players.get(playerID);
	}

	public void recieveMapHalf(String playerID, ServerHalfMap halfMap) {
		this.halfMaps.put(playerID, halfMap);
		upatePlayerHalfMapState(playerID);

	}

	private void upatePlayerHalfMapState(String playerID) {
		PlayerInformation player = players.get(playerID);
		player.sentMapHalf();
	}

	public void switchPlayersTurn(String playerID) {
		for (var eachPlayer : players.entrySet()) {
			if (eachPlayer.getKey().equals(playerID)) {
				eachPlayer.getValue().setPlayerTurnState(EPlayerTurn.WAIT_FOR_TURN);
			} else {
				eachPlayer.getValue().setPlayerTurnState(EPlayerTurn.PLAY);
			}
		}
	}

	private void makeFullMap() {
		if (this.fullMap.getMapSize() > 50) {
			return;
		}
		final int REQUIRED_NUMBER_OF_HALFMAPS = 2;
		if (this.halfMaps.size() == REQUIRED_NUMBER_OF_HALFMAPS) {
			Random random = new Random();
			List<ServerHalfMap> maps = new ArrayList<ServerHalfMap>(halfMaps.values());

			boolean changeOrderOfMaps = random.nextBoolean();

			if (changeOrderOfMaps) {
				this.fullMap = new ServerFullMap(maps.get(0), maps.get(1));
			} else {
				this.fullMap = new ServerFullMap(maps.get(1), maps.get(0));
			}
		}
	}

	public void chooseRandomFirstPlayer() {
		final int REQUIRED_NUMBER_OF_PLAYERS = 2;
		if (this.players.size() == REQUIRED_NUMBER_OF_PLAYERS) {
			List<PlayerInformation> playersInformation = new LinkedList<PlayerInformation>(this.players.values());
			Random random = new Random();
			boolean firstRegisteredPlayerFirst = random.nextBoolean();

			if (firstRegisteredPlayerFirst) {
				playersInformation.get(0).setPlayerTurnState(EPlayerTurn.PLAY);
			} else {
				playersInformation.get(1).setPlayerTurnState(EPlayerTurn.PLAY);
			}
		}
	}

}
