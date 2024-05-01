package rules;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;

import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import server.exceptions.InvalidCastleCountPresentException;

class HalfMapContainsOneCastleRuleTest {

	@Test
	@MethodSource("createMapWithoutCastle")
	void halfMapHasNoCastle_checkForCastleCount_ThrowInvalidCastleCountPresentException() {
		PlayerHalfMap invalidMap = createMapWithoutCastle();
		HalfMapContainsOneCastleRule rule = new HalfMapContainsOneCastleRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidCastleCountPresentException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));

	}

	@Test
	@MethodSource("createMapWithTwoCastles")
	void halfMapHasTwoCastles_checkForCastleCount_ThrowInvalidCastleCountPresentException() {
		PlayerHalfMap invalidMap = createMapWithTwoCastles();
		HalfMapContainsOneCastleRule rule = new HalfMapContainsOneCastleRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidCastleCountPresentException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));
	}

	@Test
	@MethodSource("createMapWithOneCastle")
	void halfMapHasOneCastle_checkForCastleCount_NoExceptionThrown() {
		PlayerHalfMap halfMap = createMapWithOneCastle();
		HalfMapContainsOneCastleRule rule = new HalfMapContainsOneCastleRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertDoesNotThrow(() -> rule.validateMap(fakeGameID, halfMap));
	}

	private static PlayerHalfMap createMapWithoutCastle() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);
				nodes.add(mapNode);
			}
		}
		return new PlayerHalfMap("playeID", nodes);
	}

	private static PlayerHalfMap createMapWithTwoCastles() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);

				if ((xCoordinate == 1 && yCoordinate == 1) || (xCoordinate == 3 && yCoordinate == 3)) {
					// random choosen coordinates (no meaning)
					mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, true, ETerrain.Grass);
				}
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playeID", nodes);
	}

	private static PlayerHalfMap createMapWithOneCastle() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);

				if (xCoordinate == 3 && yCoordinate == 3) {
					// random choosen coordinates (no meaning)
					mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, true, ETerrain.Grass);
				}
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playeID", nodes);
	}

}
