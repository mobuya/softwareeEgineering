package network;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import data.EPlayerState;
import map.EField;
import map.Position;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import move.EDirection;

class NetworkDataConverterTest {
	private NetworkDataConverter converter = new NetworkDataConverter();

	@Test
	@MethodSource("createPlayerHalfMap")
	void finishedClientHalfMap_converitngtoServerMap_MapSizeshouldBe50() {
		LinkedHashMap<Position, EField> clientHalfMap = createPlayerHalfMap();

		PlayerHalfMap serverMapHalf = converter.converMyMapToServerMap(clientHalfMap, "playerID");

		Assertions.assertEquals(50, serverMapHalf.getMapNodes().size());
	}

	@Test
	void treasureFound_extractInformationFromGameState_ReturnTrasureFoundTrue() {
		GameState mockedGameState = Mockito.mock(GameState.class);
		PlayerState mockedState = Mockito.mock(PlayerState.class);
		Mockito.when(mockedGameState.getPlayers()).thenReturn(Set.of(mockedState));
		Mockito.when(mockedState.getUniquePlayerID()).thenReturn("mockedPlayerID");
		Mockito.when(mockedState.hasCollectedTreasure()).thenReturn(true);

		boolean trasureCollected = converter.extractTreasureState(mockedGameState, "mockedPlayerID");

		Assertions.assertEquals(true, trasureCollected);
	}

	@ParameterizedTest
	@CsvSource({ "MustAct, SEND_ACTION", "MustWait, WAIT", "Won, WON", "Lost, LOST" })
	void unknownPlayerState_extractFromGameState_ReturnClientPlayerState(EPlayerGameState serverState,
			EPlayerState clientState) {
		GameState mockedGameState = Mockito.mock(GameState.class);
		PlayerState mockedState = Mockito.mock(PlayerState.class);
		Mockito.when(mockedGameState.getPlayers()).thenReturn(Set.of(mockedState));
		Mockito.when(mockedState.getUniquePlayerID()).thenReturn("mockedPlayerID");
		Mockito.when(mockedState.getState()).thenReturn(serverState);
		// EPlayerGameState
		EPlayerState playerState = converter.extractPlayerState(mockedGameState, "mockedPlayerID");

		Assertions.assertEquals(clientState, playerState);
	}

	@Test
	@MethodSource("createPlayerHalfMap")
	void withNewgamestate_extractFullMap_MapTilesShouldMatch() {
		GameState mockedGameState = Mockito.mock(GameState.class);
		FullMap mockedServerMap = Mockito.mock(FullMap.class);
		FullMapNode mockedNodeOne = Mockito.mock(FullMapNode.class);
		FullMapNode mockedNodeTwo = Mockito.mock(FullMapNode.class);

		Mockito.when(mockedGameState.getMap()).thenReturn(mockedServerMap);
		Mockito.when(mockedServerMap.getMapNodes()).thenReturn(Arrays.asList(mockedNodeOne, mockedNodeTwo));

		Mockito.when(mockedNodeOne.getX()).thenReturn(1);
		Mockito.when(mockedNodeOne.getY()).thenReturn(1);
		Mockito.when(mockedNodeOne.getTerrain()).thenReturn(ETerrain.Grass);

		Mockito.when(mockedNodeTwo.getX()).thenReturn(2);
		Mockito.when(mockedNodeTwo.getY()).thenReturn(1);
		Mockito.when(mockedNodeTwo.getTerrain()).thenReturn(ETerrain.Mountain);

		LinkedHashMap<Position, EField> convertedbaseMap = converter.extractBaseMap(mockedGameState);

		Assertions.assertTrue(convertedbaseMap.containsKey(new Position(1, 1)));
		Assertions.assertTrue(convertedbaseMap.containsKey(new Position(2, 1)));

	}

	@ParameterizedTest
	@CsvSource({ "DOWN, Down", "UP, Up", "LEFT, Left", "RIGHT, Right" })
	void clientWantedMove_converttoServerMove_ReturnValidMove(EDirection clientDirection, EMove serverMove) {
		PlayerMove move = converter.convertDirectionToServerMove("playerID", clientDirection);

		Assertions.assertEquals(move.getMove(), serverMove);
	}

	private static LinkedHashMap<Position, EField> createPlayerHalfMap() {
		LinkedHashMap<Position, EField> halfMap = new LinkedHashMap<>();
		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		for (int yCounter = 0; yCounter < Y_LIMIT; yCounter++) {
			for (int xCounter = 0; xCounter < X_LIMIT; xCounter++) {
				Position newPosition = new Position(xCounter, yCounter);
				EField field = EField.values()[new Random().nextInt(EField.values().length)];
				halfMap.put(newPosition, field);
			}
		}

		return halfMap;
	}
}
