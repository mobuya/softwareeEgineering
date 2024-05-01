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
import server.exceptions.InvalidWaterFieldBorderCountException;

class WaterFieldsOnBorderCountRuleTest {

	@Test
	@MethodSource("createMapWithWaterOnHorizontalBorder")
	void clientSentHalfMapWithWaterOnHorizontalBorder_checkForWaterBordercCount_ThrowInvalidWaterFieldBorderCountException() {
		PlayerHalfMap invalidMap = createMapWithWaterOnHorizontalBorder();
		WaterFieldsOnBorderCountRule rule = new WaterFieldsOnBorderCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidWaterFieldBorderCountException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));

	}

	@Test
	@MethodSource("createMapWithWaterOnVerticalBorder")
	void clientSentHalfMapWithWaterOnVerticalBorder_checkForWaterBordercCount_ThrowInvalidWaterFieldBorderCountException() {
		PlayerHalfMap invalidMap = createMapWithWaterOnVerticalBorder();
		WaterFieldsOnBorderCountRule rule = new WaterFieldsOnBorderCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertThrows(InvalidWaterFieldBorderCountException.class,
				() -> rule.validateMap(fakeGameID, invalidMap));

	}

	@Test
	@MethodSource("createMapWithNoWaterOnBorder")
	void clientSentHalfMapWithNoWaterOnBorder_checkForWaterBordercCount_NoExceptionThrown() {
		PlayerHalfMap halfMap = createMapWithNoWaterOnBorder();
		WaterFieldsOnBorderCountRule rule = new WaterFieldsOnBorderCountRule();
		UniqueGameIdentifier fakeGameID = new UniqueGameIdentifier("12345");

		Assertions.assertDoesNotThrow(() -> rule.validateMap(fakeGameID, halfMap));

	}

	private static PlayerHalfMap createMapWithWaterOnHorizontalBorder() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				ETerrain terrain = ETerrain.Grass;

				if (yCoordinate == 0 && xCoordinate % 2 != 0) {
					terrain = ETerrain.Water;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playeID", nodes);
	}

	private static PlayerHalfMap createMapWithWaterOnVerticalBorder() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				ETerrain terrain = ETerrain.Grass;

				if (xCoordinate == 0) {
					terrain = ETerrain.Water;
				}

				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, terrain);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playeID", nodes);
	}

	private static PlayerHalfMap createMapWithNoWaterOnBorder() {
		Collection<PlayerHalfMapNode> nodes = new HashSet<>();

		for (int xCoordinate = 0; xCoordinate < 10; xCoordinate++) {
			for (int yCoordinate = 0; yCoordinate < 5; yCoordinate++) {
				PlayerHalfMapNode mapNode = new PlayerHalfMapNode(xCoordinate, yCoordinate, ETerrain.Grass);
				nodes.add(mapNode);
			}
		}

		return new PlayerHalfMap("playeID", nodes);
	}

}
