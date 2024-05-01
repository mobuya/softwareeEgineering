package game.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.Game;
import game.data.map.Position;
import game.data.map.ServerFullMap;
import game.data.map.ServerHalfMap;
import game.data.map.constants.EField;
import game.data.map.constants.EMapEntities;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class DataConverter {
	private final Logger logger = LoggerFactory.getLogger(Game.class);

	public UniqueGameIdentifier extractUniqueGameID(Game newGame) {
		String gameID = newGame.getGameId();
		UniqueGameIdentifier uniqueGameID = new UniqueGameIdentifier(gameID);

		return uniqueGameID;
	}

	public PlayerInformation extractPlayerInfoFromRegistration(PlayerRegistration playerRegistration) {
		String firstName = playerRegistration.getStudentFirstName();
		String lastName = playerRegistration.getStudentLastName();
		String uaccount = playerRegistration.getStudentUAccount();

		return new PlayerInformation(firstName, lastName, uaccount);
	}

	public GameState convertGameToGameState(Game currentGame, UniquePlayerIdentifier uniquePlayerID) {
		String playerID = uniquePlayerID.getUniquePlayerID();
		String gamestateID = currentGame.getGameStateID();

		if (!currentGame.getPlayers().isEmpty()) {

			Set<PlayerState> activePlayers = convertPlayersToSet(currentGame.getPlayers(), uniquePlayerID);
			GameState state = new GameState(activePlayers, gamestateID);

			ServerFullMap fullMap = currentGame.getFullMapForPlayerID(playerID);
			final int SIZE_WITH_ONE_HALFMAP = 50;
			if (fullMap.getMapSize() > SIZE_WITH_ONE_HALFMAP) {
				state = new GameState(convertServerMapToClientMap(fullMap), activePlayers, gamestateID);
			}

			return state;
		}

		return new GameState();
	}

	private PlayerState convertPlayerInformationToPlayerState(String playerID, PlayerInformation playerInfo) {
		String playerFirstname = playerInfo.getFirstName();
		String playerLastName = playerInfo.getLastName();
		String uAccount = playerInfo.getUaccount();

		EPlayerGameState playerState = convertPlayerTurnToPlayerGameState(playerInfo.getPlayerTurnState());

		boolean treasureState = playerInfo.getTreasureState();

		UniquePlayerIdentifier uniquePlayerID = new UniquePlayerIdentifier(playerID);
		PlayerState returnState = new PlayerState(playerFirstname, playerLastName, uAccount, playerState,
				uniquePlayerID, treasureState);
		return returnState;
	}

	private EPlayerGameState convertPlayerTurnToPlayerGameState(EPlayerTurn turn) {
		EPlayerGameState playerGameState = EPlayerGameState.MustWait;
		switch (turn) {
		case LOST:
			playerGameState = EPlayerGameState.Lost;
			break;
		case PLAY:
			playerGameState = EPlayerGameState.MustAct;
			break;
		case WAIT_FOR_TURN:
			playerGameState = EPlayerGameState.MustWait;
			break;
		case WON:
			playerGameState = EPlayerGameState.Won;
			break;
		}
		return playerGameState;

	}

	private Set<PlayerState> convertPlayersToSet(Map<String, PlayerInformation> activePlayers,
			UniquePlayerIdentifier uniquePlayerID) {
		Set<PlayerState> players = new HashSet<>();

		String playerID = uniquePlayerID.getUniquePlayerID();

		for (var eachPlayer : activePlayers.entrySet()) {
			if (eachPlayer.getKey().equals(playerID)) {
				PlayerState requestedPlayer = convertPlayerInformationToPlayerState(playerID, eachPlayer.getValue());
				players.add(requestedPlayer);
			} else {
				PlayerState secondPlayer = convertPlayerInformationToPlayerState("fakePlayerID", eachPlayer.getValue());
				players.add(secondPlayer);
			}
		}

		return players;
	}

	public ServerHalfMap convertClientMapToServerMap(PlayerHalfMap halfMap) {
		LinkedHashMap<Position, EField> baseMap = new LinkedHashMap<>();
		Position playerPosition = new Position();
		Collection<PlayerHalfMapNode> playerMapNodes = halfMap.getMapNodes();

		for (var eachNode : playerMapNodes) {
			Position position = new Position(eachNode.getX(), eachNode.getY());

			if (eachNode.isFortPresent()) {
				playerPosition = new Position(eachNode.getX(), eachNode.getY());
			}

			EField field = convertTerrainToField(eachNode.getTerrain());
			baseMap.put(position, field);
		}

		return new ServerHalfMap(baseMap, playerPosition);
	}

	private EField convertTerrainToField(ETerrain terrain) {
		EField field = EField.GRASS;
		switch (terrain) {
		case Mountain:
			field = EField.MOUNTAIN;
			break;
		case Water:
			field = EField.WATER;
			break;
		default:
			break;
		}

		return field;
	}

	public FullMap convertServerMapToClientMap(ServerFullMap serverMap) {
		Collection<FullMapNode> nodes = new HashSet<>();
		Map<Position, EField> baseMap = serverMap.getBaseMap();

		logger.debug("The size of the server map before converting is: {}", serverMap.getBaseMap().size());

		for (var eachMapTile : baseMap.entrySet()) {

			ETerrain terrain = convertFieldToTerrain(eachMapTile.getValue());
			Position position = eachMapTile.getKey();

			EMapEntities entity = serverMap.findEntityOnPosition(position);
			EPlayerPositionState state = convertMapEntityToPositionState(entity);

			EMapEntities castle = serverMap.findCastleOnPosition(position);
			EFortState fortState = convertEntityCastleToFortState(castle);

			ETreasureState treasureState = ETreasureState.NoOrUnknownTreasureState;

			Position playersPosition = serverMap.getEntityPosition(EMapEntities.MY_PLAYER);
			if (isTreasurevisible(serverMap, playersPosition)) {
				treasureState = ETreasureState.MyTreasureIsPresent;
			}

			FullMapNode node = new FullMapNode(terrain, state, treasureState, fortState, position.getXCoordinate(),
					position.getYCoordinate());

			nodes.add(node);
		}

		logger.debug("The number of nodes I added to the converted Full Map is: {}", nodes.size());

		return new FullMap(nodes);

	}

	private boolean isTreasurevisible(ServerFullMap sFullMap, Position playerPosition) {
		boolean tresureVisible = false;
		Position treasurePosition = sFullMap.getTreasurePosition();
		List<Position> surroundingFields = getsurroundingFields(sFullMap, playerPosition);

		EField playerPositionField = sFullMap.getBaseMap().get(playerPosition);

		if (playerPosition.equals(treasurePosition)) {
			tresureVisible = true;
		}

		if (playerPositionField.equals(EField.MOUNTAIN) && surroundingFields.contains(treasurePosition)) {
			tresureVisible = true;
		}

		return tresureVisible;
	}

	private ETerrain convertFieldToTerrain(EField field) {
		ETerrain terrain = ETerrain.Grass;
		switch (field) {
		case MOUNTAIN:
			terrain = ETerrain.Mountain;
			break;
		case WATER:
			terrain = ETerrain.Water;
			break;
		default:
			break;
		}
		return terrain;
	}

	private EPlayerPositionState convertMapEntityToPositionState(EMapEntities entity) {
		EPlayerPositionState state = EPlayerPositionState.NoPlayerPresent;
		switch (entity) {
		case MY_PLAYER:
			state = EPlayerPositionState.MyPlayerPosition;
			break;
		case ENEMY:
			state = EPlayerPositionState.EnemyPlayerPosition;
			break;
		case BOTH:
			state = EPlayerPositionState.BothPlayerPosition;
			break;
		default:
			break;
		}
		return state;
	}

	private EFortState convertEntityCastleToFortState(EMapEntities entity) {
		EFortState state = EFortState.NoOrUnknownFortState;
		switch (entity) {
		case ENEMY:
			state = EFortState.EnemyFortPresent;
			break;
		case MY_PLAYER:
			state = EFortState.MyFortPresent;
			break;
		case UNKNOWN:
			state = EFortState.NoOrUnknownFortState;
			break;
		default:
			break;
		}
		return state;
	}

	private List<Position> getsurroundingFields(ServerFullMap sFullMap, Position playerPosition) {
		List<Position> surroundingFields = new ArrayList<>();
		final int currentXCoordinate = playerPosition.getXCoordinate();
		final int currentYCoordinate = playerPosition.getYCoordinate();

		Position north = new Position(currentXCoordinate, currentYCoordinate - 1);
		Position northEast = new Position(currentXCoordinate - 1, currentYCoordinate - 1);
		Position east = new Position(currentXCoordinate - 1, currentYCoordinate);
		Position southEast = new Position(currentXCoordinate - 1, currentYCoordinate + 1);
		Position south = new Position(currentXCoordinate, currentYCoordinate + 1);
		Position southWest = new Position(currentXCoordinate + 1, currentYCoordinate + 1);
		Position west = new Position(currentXCoordinate + 1, currentYCoordinate);
		Position northWest = new Position(currentXCoordinate + 1, currentYCoordinate - 1);

		surroundingFields.addAll(List.of(north, northEast, east, southEast, south, southWest, west, northWest));

		return surroundingFields;

	}
}
