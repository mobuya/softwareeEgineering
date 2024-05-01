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
import server.exceptions.InvalidMapHalfSizeException;

class ValidHalfMapSizeRuleTest {

	@Test
	@MethodSource("createMapWith25Fields")
	void playerSentMapHalfWithSmallerSize_checkMapHalfSize_ThrowInvalidMapHalfSizeException() {
		PlayerHalfMap invalidMap = createMapWith25Fields();
		ValidHalfMapSizeRule rule = new ValidHalfMapSizeRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidMapHalfSizeException.class, () -> rule.validateMap(fakeGameID, invalidMap));

	}

	@Test
	@MethodSource("createMapWith50Fields")
	void playerSentMapHalfWithCorrectSize_checkMapHalfSIze_NoExceptionThrown() {
		PlayerHalfMap validMap = createMapWith50Fields();
		ValidHalfMapSizeRule rule = new ValidHalfMapSizeRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertDoesNotThrow(() -> rule.validateMap(fakeGameID, validMap));

	}

	private static PlayerHalfMap createMapWith25Fields() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		final int X_LIMIT = 5;
		final int Y_LIMIT = 5;

		for (int xCoordinate = 0; xCoordinate < X_LIMIT; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < Y_LIMIT; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}

	private static PlayerHalfMap createMapWith50Fields() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		final int X_LIMIT = 10;
		final int Y_LIMIT = 5;

		for (int xCoordinate = 0; xCoordinate < X_LIMIT; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < Y_LIMIT; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}
}
