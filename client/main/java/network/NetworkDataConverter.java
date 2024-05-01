package network;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import data.EPlayerState;
import exceptions.InvalidMapCharacterPosition;
import map.EField;
import map.EMapCharacter;
import map.FullMap;
import map.Position;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import move.EDirection;

public class NetworkDataConverter {
	Logger logger = LoggerFactory.getLogger(NetworkDataConverter.class);

	public PlayerHalfMap converMyMapToServerMap(LinkedHashMap<Position, EField> halfMap, String playerID) {
		Set<PlayerHalfMapNode> clientHalfMapNodes = new HashSet<>();
		for (Map.Entry<Position, EField> field : halfMap.entrySet()) {

			ETerrain terrain = ETerrain.Water;
			if (field.getValue() == EField.GRASS) {
				terrain = ETerrain.Grass;
			}
			if (field.getValue() == EField.MOUNTAIN) {
				terrain = ETerrain.Mountain;
			}
			boolean hasCastle = field.getKey().hasMyCastle();
			PlayerHalfMapNode newNode = new PlayerHalfMapNode(field.getKey().getXCoordinate(),
					field.getKey().getYCoordinate(), hasCastle, terrain);
			clientHalfMapNodes.add(newNode);
		}
		PlayerHalfMap serverPlayerHalfMap = new PlayerHalfMap(playerID, clientHalfMapNodes);
		return serverPlayerHalfMap;
	}

	public boolean extractTreasureState(GameState gameState, String playerID) {
		PlayerState myPlayerState = gameState.getPlayers().stream().filter(p -> p.getUniquePlayerID().equals(playerID))
				.findFirst().get();
		return myPlayerState.hasCollectedTreasure();
	}

	public EPlayerState extractPlayerState(GameState gameState, String playerID) {
		EPlayerState state = EPlayerState.UNKNOWN;
		PlayerState myPlayerState = gameState.getPlayers().stream().filter(p -> p.getUniquePlayerID().equals(playerID))
				.findFirst().get();

		EPlayerGameState serverState = myPlayerState.getState();
		switch (serverState) {
		case MustAct:
			state = EPlayerState.SEND_ACTION;
			break;
		case MustWait:
			state = EPlayerState.WAIT;
			break;
		case Won:
			state = EPlayerState.WON;
			break;
		case Lost:
			state = EPlayerState.LOST;
			break;
		}

		if (state.equals(EPlayerState.UNKNOWN)) {
			logger.error("Game State is unkown; recheck the server GameState object.");
		}
		return state;
	}

	public FullMap extractFullMap(GameState state) {
		FullMap fullMap = new FullMap(extractBaseMap(state));
		Collection<FullMapNode> serverMapNodes = state.getMap().getMapNodes();

		for (FullMapNode node : serverMapNodes) {
			if (node.getTreasureState().equals(ETreasureState.MyTreasureIsPresent)) {
				fullMap.placeTreasure(convertServerNodeToPosition(node));
			}

			EMapCharacter character = checkForCharacter(node);
			if (!character.equals(EMapCharacter.EMPTY)) {
				try {
					fullMap.placePlayerOnMap(character, convertServerNodeToPosition(node));
				} catch (InvalidMapCharacterPosition e) {
					System.err.print(e.getMessage());
				}

				// issue: when both characters on map, cant read it
				if (character.equals(EMapCharacter.BOTH)) {
					try {
						fullMap.placePlayerOnMap(EMapCharacter.MY_PLAYER, convertServerNodeToPosition(node));
					} catch (InvalidMapCharacterPosition e) {
						System.err.print(e.getMessage());
					}
					try {
						fullMap.placePlayerOnMap(EMapCharacter.ENEMY, convertServerNodeToPosition(node));
					} catch (InvalidMapCharacterPosition e) {
						System.err.print(e.getMessage());
					}
				}
			}
			EMapCharacter castlePositions = checkForCastle(node);
			if (!castlePositions.equals(EMapCharacter.EMPTY)) {
				try {
					fullMap.placeCastleOnMap(castlePositions, convertServerNodeToPosition(node));
				} catch (InvalidMapCharacterPosition e) {
					System.err.print(e.getMessage());
				}
			}
		}

		return fullMap;
	}

	public PlayerMove convertDirectionToServerMove(String playerID, EDirection direction) {
		EMove move = EMove.Up;
		switch (direction) {
		case DOWN:
			move = EMove.Down;
			break;
		case LEFT:
			move = EMove.Left;
			break;
		case RIGHT:
			move = EMove.Right;
			break;
		case UP:
			move = EMove.Up;
			break;
		}
		return PlayerMove.of(playerID, move);
	}

	private EMapCharacter checkForCastle(FullMapNode node) {
		EMapCharacter character = EMapCharacter.EMPTY;
		switch (node.getFortState()) {
		case EnemyFortPresent:
			character = EMapCharacter.ENEMY;
			break;
		case MyFortPresent:
			character = EMapCharacter.MY_PLAYER;
			break;
		case NoOrUnknownFortState:
			character = EMapCharacter.EMPTY;
			break;
		}
		return character;
	}

	private EMapCharacter checkForCharacter(FullMapNode node) {
		EMapCharacter character = EMapCharacter.EMPTY;
		switch (node.getPlayerPositionState()) {
		case BothPlayerPosition:
			character = EMapCharacter.BOTH;
			break;
		case EnemyPlayerPosition:
			character = EMapCharacter.ENEMY;
			break;
		case MyPlayerPosition:
			character = EMapCharacter.MY_PLAYER;
			break;
		case NoPlayerPresent:
			break;
		}
		return character;
	}

	public LinkedHashMap<Position, EField> extractBaseMap(GameState state) {
		LinkedHashMap<Position, EField> baseForFullMap = new LinkedHashMap<>();

		Collection<FullMapNode> serverMapNodes = state.getMap().getMapNodes();

		for (FullMapNode node : serverMapNodes) {
			Position position = convertServerNodeToPosition(node);
			EField field = convertServerTerrainToClientField(node.getTerrain());

			baseForFullMap.put(position, field);
		}
		return baseForFullMap;
	}

	private Position convertServerNodeToPosition(FullMapNode node) {
		Position position = new Position(node.getX(), node.getY());
		return position;
	}

	private EField convertServerTerrainToClientField(ETerrain serverTerrain) {
		EField field = EField.GRASS;
		switch (serverTerrain) {
		case Grass:
			field = EField.GRASS;
			break;
		case Mountain:
			field = EField.MOUNTAIN;
			break;
		case Water:
			field = EField.WATER;
			break;
		}
		return field;
	}

}
