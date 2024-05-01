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
import server.exceptions.InvalidCastleFieldException;

class CastlePositionIsGrassRuleTest {

	@Test
	@MethodSource("createMapWithCastleOnField")
	void playerSentMapHalfWithCastleOnGrass_checkCastleFieldType_NoExceptionThrown() {
		PlayerHalfMap halfMap = createMapWithCastleOnField(ETerrain.Grass);
		CastlePositionIsGrassRule rule = new CastlePositionIsGrassRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertDoesNotThrow(() -> rule.validateMap(fakeGameID, halfMap));

	}

	@Test
	@MethodSource("createMapWithCastleOnField")
	void playerSentMapHalfWithCastleOnWater_checkCastleFieldType_ThrowInvalidCastleFieldException() {
		PlayerHalfMap invalidMap = createMapWithCastleOnField(ETerrain.Water);
		CastlePositionIsGrassRule rule = new CastlePositionIsGrassRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidCastleFieldException.class, () -> rule.validateMap(fakeGameID, invalidMap));
	}

	private static PlayerHalfMap createMapWithCastleOnField(ETerrain terrain) {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();
		final int X_COORDINATE_LIMIT = 10;
		final int y_COORDINATE_LIMIT = 5;

		for (int xCounter = 0; xCounter < X_COORDINATE_LIMIT; xCounter++) {
			for (int yCounter = 0; yCounter < y_COORDINATE_LIMIT; yCounter++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCounter, yCounter, false, ETerrain.Grass);

				if (xCounter == 2 && yCounter == 2) {
					// random choosen coordinates (no meaning)
					mapNode = new PlayerHalfMapNode(xCounter, yCounter, true, terrain);
				}
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playerID", nodes);
	}

}
